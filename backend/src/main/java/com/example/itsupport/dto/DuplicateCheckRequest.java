package com.example.itsupport.dto;

import lombok.Data;

@Data
public class DuplicateCheckRequest {
    private String title;
    private String description;
    private Double threshold;
}
