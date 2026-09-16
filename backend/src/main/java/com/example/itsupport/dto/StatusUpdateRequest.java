package com.example.itsupport.dto;

import com.example.itsupport.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private Status status;
    private String comment;
}
