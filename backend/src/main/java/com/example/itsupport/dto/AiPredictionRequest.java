package com.example.itsupport.dto;

import lombok.Data;

@Data
public class AiPredictionRequest {
    private String title;
    private String description;
    private String category;
}
