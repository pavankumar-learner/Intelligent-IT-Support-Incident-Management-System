package com.example.itsupport.service;

import com.example.itsupport.dto.*;
import com.example.itsupport.entity.*;
import com.example.itsupport.exception.BadRequestException;
import com.example.itsupport.exception.UnauthorizedException;
import com.example.itsupport.repository.IncidentCommentRepository;
import com.example.itsupport.repository.IncidentHistoryRepository;
import com.example.itsupport.repository.IncidentRepository;
import com.example.itsupport.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IncidentCommentRepository commentRepository;

    @Mock
    private IncidentHistoryRepository historyRepository;

    @Mock
    private AiIntegrationService aiIntegrationService;

    @InjectMocks
    private IncidentService incidentService;

    private User employeeUser;
    private User agentUser;
    private Incident sampleIncident;

    @BeforeEach
    void setUp() {
        employeeUser = User.builder()
                .id(10L)
                .name("Alice Employee")
                .email("alice@enterprise.com")
                .role(Role.ROLE_EMPLOYEE)
                .department("HR")
                .active(true)
                .build();

        agentUser = User.builder()
                .id(20L)
                .name("Bob Support")
                .email("bob@itsupport.com")
                .role(Role.ROLE_SUPPORT_AGENT)
                .department("IT Helpdesk")
                .active(true)
                .build();

        sampleIncident = Incident.builder()
                .id(5L)
                .ticketNumber("INC-1005")
                .title("Wi-Fi keeps dropping")
                .description("Cannot stay connected in Conference Room B")
                .category(Category.Network)
                .priority(Priority.HIGH)
                .status(Status.NEW)
                .createdBy(employeeUser)
                .department("HR")
                .location("Conference Room B")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createIncident saves ticket and assigns collision-free INC-{1000 + ID} ticket number")
    void testCreateIncidentGeneratesCorrectTicketNumber() {
        IncidentRequest req = new IncidentRequest();
        req.setTitle("Monitor will not turn on");
        req.setDescription("Power button shows no LED indicator");
        req.setLocation("Bay 3");

        when(userRepository.findByEmail("alice@enterprise.com")).thenReturn(Optional.of(employeeUser));
        when(aiIntegrationService.predictCategory(any())).thenReturn(
                AiPredictionResponse.builder().prediction("Hardware").confidence(0.92).build()
        );
        when(aiIntegrationService.predictPriority(any())).thenReturn(
                AiPredictionResponse.builder().prediction("MEDIUM").confidence(0.85).build()
        );

        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> {
            Incident inc = invocation.getArgument(0);
            if (inc.getId() == null) {
                inc.setId(12L); // simulate database-generated auto increment ID
            }
            return inc;
        });

        IncidentResponse response = incidentService.createIncident(req, "alice@enterprise.com");

        assertNotNull(response);
        assertEquals("INC-1012", response.getTicketNumber(), "Ticket number should be deterministic: INC-(1000 + id)");
        assertEquals(Category.Hardware, response.getCategory());
        assertEquals(Priority.MEDIUM, response.getPriority());
        assertEquals(Status.NEW, response.getStatus());

        verify(historyRepository, times(1)).save(any(IncidentHistory.class));
    }

    @Test
    @DisplayName("Employee cannot override incident priority")
    void testEmployeeCannotOverridePriority() {
        PriorityUpdateRequest req = new PriorityUpdateRequest();
        req.setPriority(Priority.CRITICAL);

        assertThrows(UnauthorizedException.class, () ->
                incidentService.updatePriority(5L, req, employeeUser)
        );
        verify(incidentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Invalid status transition (e.g. NEW directly to CLOSED) throws BadRequestException")
    void testInvalidStatusTransitionThrowsBadRequest() {
        when(incidentRepository.findById(5L)).thenReturn(Optional.of(sampleIncident));

        StatusUpdateRequest req = new StatusUpdateRequest();
        req.setStatus(Status.CLOSED); // Cannot close a NEW ticket without resolving it first

        assertThrows(BadRequestException.class, () ->
                incidentService.updateStatus(5L, req, agentUser)
        );
    }

    @Test
    @DisplayName("Valid status transition (NEW to ASSIGNED) succeeds")
    void testValidStatusTransitionSucceeds() {
        when(incidentRepository.findById(5L)).thenReturn(Optional.of(sampleIncident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StatusUpdateRequest req = new StatusUpdateRequest();
        req.setStatus(Status.ASSIGNED);
        req.setComment("Assigned to Tier 1 technician");

        IncidentResponse updated = incidentService.updateStatus(5L, req, agentUser);

        assertNotNull(updated);
        assertEquals(Status.ASSIGNED, updated.getStatus());
        verify(historyRepository, times(1)).save(any(IncidentHistory.class));
    }
}

