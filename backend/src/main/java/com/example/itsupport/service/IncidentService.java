package com.example.itsupport.service;

import com.example.itsupport.dto.*;
import com.example.itsupport.entity.*;
import com.example.itsupport.exception.BadRequestException;
import com.example.itsupport.exception.ResourceNotFoundException;
import com.example.itsupport.exception.UnauthorizedException;
import com.example.itsupport.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final IncidentCommentRepository commentRepository;
    private final IncidentHistoryRepository historyRepository;
    private final AiIntegrationService aiIntegrationService;

    @Transactional
    public IncidentResponse createIncident(IncidentRequest request, String currentUserEmail) {
        User creator = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserEmail));

        // Run AI Prediction
        AiPredictionRequest aiReq = new AiPredictionRequest();
        aiReq.setTitle(request.getTitle());
        aiReq.setDescription(request.getDescription());

        AiPredictionResponse categoryPred = aiIntegrationService.predictCategory(aiReq);
        AiPredictionResponse priorityPred = aiIntegrationService.predictPriority(aiReq);

        Category predictedCat = Category.fromString(categoryPred.getPrediction());
        Priority predictedPri = Priority.fromString(priorityPred.getPrediction());

        // Use requested Category/Priority or AI defaults
        Category finalCat = request.getCategory() != null ? request.getCategory() : predictedCat;
        Priority finalPri = request.getPriority() != null ? request.getPriority() : predictedPri;

        Incident incident = Incident.builder()
                .ticketNumber("PENDING") // will be set after save using the generated ID
                .title(request.getTitle())
                .description(request.getDescription())
                .category(finalCat)
                .predictedCategory(predictedCat)
                .categoryConfidence(categoryPred.getConfidence())
                .priority(finalPri)
                .predictedPriority(predictedPri)
                .priorityConfidence(priorityPred.getConfidence())
                .status(Status.NEW)
                .createdBy(creator)
                .department(request.getDepartment() != null ? request.getDepartment() : creator.getDepartment())
                .location(request.getLocation() != null ? request.getLocation() : "HQ Main Building")
                .build();

        Incident saved = incidentRepository.save(incident);

        // Assign a unique, collision-free ticket number based on the auto-generated database ID
        saved.setTicketNumber("INC-" + (1000 + saved.getId()));
        saved = incidentRepository.save(saved);

        // Record Initial Audit History
        recordHistory(saved, creator, null, Status.NEW, null, finalPri, "Incident created by employee");

        return IncidentResponse.fromEntity(saved);
    }

    public List<IncidentResponse> getAllIncidents(String search, Category category, Priority priority, Status status, String department, User currentUser) {
        List<Incident> incidents;

        // Role-based visibility scoping
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE) {
            incidents = incidentRepository.findByCreatedBy(currentUser);
        } else {
            incidents = incidentRepository.findAll();
        }

        // Apply filters in memory for robust query handling
        return incidents.stream()
                .filter(i -> search == null || search.isBlank() ||
                        i.getTicketNumber().toLowerCase().contains(search.toLowerCase()) ||
                        i.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(search.toLowerCase()))
                .filter(i -> category == null || i.getCategory() == category)
                .filter(i -> priority == null || i.getPriority() == priority)
                .filter(i -> status == null || i.getStatus() == status)
                .filter(i -> department == null || department.isBlank() || i.getDepartment().equalsIgnoreCase(department))
                .sorted(Comparator.comparing(Incident::getCreatedAt).reversed())
                .map(IncidentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public IncidentResponse getIncidentById(Long id, User currentUser) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + id));

        // Enforce employee security policy (employees can only view their own incidents)
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE && !incident.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to view this incident ticket.");
        }

        return IncidentResponse.fromEntity(incident);
    }

    @Transactional
    public IncidentResponse assignAgent(Long incidentId, Long agentId, User currentUser) {
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE) {
            throw new UnauthorizedException("Employees cannot assign incident tickets.");
        }

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + incidentId));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Support agent not found with ID: " + agentId));

        if (agent.getRole() != Role.ROLE_SUPPORT_AGENT && agent.getRole() != Role.ROLE_ADMIN) {
            throw new BadRequestException("Selected user is not a Support Agent or Admin.");
        }

        Status oldStatus = incident.getStatus();
        incident.setAssignedTo(agent);

        // Auto transition status from NEW -> ASSIGNED
        if (incident.getStatus() == Status.NEW) {
            incident.setStatus(Status.ASSIGNED);
        }

        Incident updated = incidentRepository.save(incident);
        recordHistory(updated, currentUser, oldStatus, updated.getStatus(), updated.getPriority(), updated.getPriority(), "Ticket assigned to " + agent.getName());

        return IncidentResponse.fromEntity(updated);
    }

    @Transactional
    public IncidentResponse updateStatus(Long incidentId, StatusUpdateRequest request, User currentUser) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + incidentId));

        Status oldStatus = incident.getStatus();
        Status newStatus = request.getStatus();

        // Validate lifecycle transition rules
        validateStatusTransition(oldStatus, newStatus);

        incident.setStatus(newStatus);
        if (newStatus == Status.RESOLVED || newStatus == Status.CLOSED) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        Incident updated = incidentRepository.save(incident);
        String note = request.getComment() != null ? request.getComment() : "Status updated from " + oldStatus + " to " + newStatus;
        recordHistory(updated, currentUser, oldStatus, newStatus, updated.getPriority(), updated.getPriority(), note);

        if (request.getComment() != null && !request.getComment().isBlank()) {
            addComment(incidentId, request.getComment(), currentUser);
        }

        return IncidentResponse.fromEntity(updated);
    }

    @Transactional
    public IncidentResponse updatePriority(Long incidentId, PriorityUpdateRequest request, User currentUser) {
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE) {
            throw new UnauthorizedException("Employees cannot override incident priority.");
        }

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + incidentId));

        Priority oldPriority = incident.getPriority();
        Priority newPriority = request.getPriority();

        incident.setPriority(newPriority);
        Incident updated = incidentRepository.save(incident);

        String note = request.getComment() != null ? request.getComment() : "Priority changed from " + oldPriority + " to " + newPriority;
        recordHistory(updated, currentUser, updated.getStatus(), updated.getStatus(), oldPriority, newPriority, note);

        return IncidentResponse.fromEntity(updated);
    }

    @Transactional
    public CommentResponse addComment(Long incidentId, String commentText, User currentUser) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + incidentId));

        IncidentComment comment = IncidentComment.builder()
                .incident(incident)
                .user(currentUser)
                .comment(commentText)
                .build();

        IncidentComment saved = commentRepository.save(comment);
        return CommentResponse.fromEntity(saved);
    }

    public List<CommentResponse> getComments(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + incidentId));

        return commentRepository.findByIncidentOrderByCreatedAtAsc(incident).stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<IncidentHistoryDto> getHistory(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + incidentId));

        return historyRepository.findByIncidentOrderByChangedAtAsc(incident).stream()
                .map(IncidentHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void recordHistory(Incident incident, User user, Status oldStatus, Status newStatus, Priority oldPriority, Priority newPriority, String note) {
        IncidentHistory history = IncidentHistory.builder()
                .incident(incident)
                .changedBy(user)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .oldPriority(oldPriority)
                .newPriority(newPriority)
                .actionNote(note)
                .build();

        historyRepository.save(history);
    }

    private void validateStatusTransition(Status current, Status target) {
        if (current == target) return;

        // Transition matrix validation
        if (current == Status.NEW && target != Status.ASSIGNED && target != Status.IN_PROGRESS) {
            throw new BadRequestException("New incidents must first be ASSIGNED or put IN_PROGRESS.");
        }
        if (current == Status.CLOSED) {
            throw new BadRequestException("Closed incidents cannot be modified directly.");
        }
        if (current == Status.RESOLVED && target != Status.CLOSED && target != Status.REOPENED) {
            throw new BadRequestException("Resolved incidents can only be CLOSED or REOPENED.");
        }
    }
}
