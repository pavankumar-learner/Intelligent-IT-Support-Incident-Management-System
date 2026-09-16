package com.example.itsupport.dto;

import com.example.itsupport.entity.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriorityUpdateRequest {
    @NotNull(message = "Priority is required")
    private Priority priority;
    private String comment;
}
