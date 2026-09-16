package com.example.itsupport.dto;

import com.example.itsupport.entity.IncidentComment;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class CommentRequest {
    @NotBlank(message = "Comment text cannot be blank")
    private String comment;
}
