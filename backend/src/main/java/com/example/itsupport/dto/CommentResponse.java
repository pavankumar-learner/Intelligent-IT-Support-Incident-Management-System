package com.example.itsupport.dto;

import com.example.itsupport.entity.IncidentComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private Long incidentId;
    private UserDto user;
    private String comment;
    private LocalDateTime createdAt;

    public static CommentResponse fromEntity(IncidentComment entity) {
        if (entity == null) return null;
        return CommentResponse.builder()
                .id(entity.getId())
                .incidentId(entity.getIncident().getId())
                .user(UserDto.fromEntity(entity.getUser()))
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
