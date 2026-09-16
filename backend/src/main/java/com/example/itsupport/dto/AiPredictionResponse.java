package com.example.itsupport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiPredictionResponse {
    private String prediction;
    private Double confidence;
    private List<String> topKeywords;
}
