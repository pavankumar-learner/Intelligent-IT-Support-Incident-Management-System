package com.example.itsupport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateCheckResponse {
    private boolean isDuplicate;
    private Double similarityScore;
    private String matchedTicketNumber;
    private String matchedTitle;
    private String message;
}
