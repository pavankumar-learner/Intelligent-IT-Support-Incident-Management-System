package com.example.itsupport.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRequest {
    @NotNull(message = "Agent ID is required")
    private Long agentId;
}
