package com.example.itsupport.dto;

import com.example.itsupport.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentResponse {
    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private Category category;
    private String categoryDisplay;
    private Category predictedCategory;
    private Double categoryConfidence;
    private Priority priority;
    private Priority predictedPriority;
    private Double priorityConfidence;
    private Status status;
    private UserDto createdBy;
    private UserDto assignedTo;
    private String department;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    public static IncidentResponse fromEntity(Incident incident) {
        if (incident == null) return null;
        return IncidentResponse.builder()
                .id(incident.getId())
                .ticketNumber(incident.getTicketNumber())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .category(incident.getCategory())
                .categoryDisplay(incident.getCategory() != null ? incident.getCategory().toDisplayString() : "Other")
                .predictedCategory(incident.getPredictedCategory())
                .categoryConfidence(incident.getCategoryConfidence())
                .priority(incident.getPriority())
                .predictedPriority(incident.getPredictedPriority())
                .priorityConfidence(incident.getPriorityConfidence())
                .status(incident.getStatus())
                .createdBy(UserDto.fromEntity(incident.getCreatedBy()))
                .assignedTo(UserDto.fromEntity(incident.getAssignedTo()))
                .department(incident.getDepartment())
                .location(incident.getLocation())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .resolvedAt(incident.getResolvedAt())
                .build();
    }
}
