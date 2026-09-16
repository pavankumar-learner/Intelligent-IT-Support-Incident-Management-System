package com.example.itsupport.dto;

import com.example.itsupport.entity.IncidentHistory;
import com.example.itsupport.entity.Priority;
import com.example.itsupport.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentHistoryDto {
    private Long id;
    private Long incidentId;
    private UserDto changedBy;
    private Status oldStatus;
    private Status newStatus;
    private Priority oldPriority;
    private Priority newPriority;
    private String actionNote;
    private LocalDateTime changedAt;

    public static IncidentHistoryDto fromEntity(IncidentHistory history) {
        if (history == null) return null;
        return IncidentHistoryDto.builder()
                .id(history.getId())
                .incidentId(history.getIncident().getId())
                .changedBy(UserDto.fromEntity(history.getChangedBy()))
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .oldPriority(history.getOldPriority())
                .newPriority(history.getNewPriority())
                .actionNote(history.getActionNote())
                .changedAt(history.getChangedAt())
                .build();
    }
}
