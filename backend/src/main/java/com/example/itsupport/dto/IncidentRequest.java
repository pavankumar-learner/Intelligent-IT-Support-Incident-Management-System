package com.example.itsupport.dto;

import com.example.itsupport.entity.Category;
import com.example.itsupport.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IncidentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private Category category;
    private Priority priority;

    @NotBlank(message = "Location is required")
    private String location;

    private String department;
}
