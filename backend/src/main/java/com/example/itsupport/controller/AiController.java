package com.example.itsupport.controller;

import com.example.itsupport.dto.*;
import com.example.itsupport.service.AiIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiIntegrationService aiService;

    @PostMapping("/predict-category")
    public ResponseEntity<AiPredictionResponse> predictCategory(@RequestBody AiPredictionRequest request) {
        return ResponseEntity.ok(aiService.predictCategory(request));
    }

    @PostMapping("/predict-priority")
    public ResponseEntity<AiPredictionResponse> predictPriority(@RequestBody AiPredictionRequest request) {
        return ResponseEntity.ok(aiService.predictPriority(request));
    }

    @PostMapping("/check-duplicate")
    public ResponseEntity<DuplicateCheckResponse> checkDuplicate(@RequestBody DuplicateCheckRequest request) {
        return ResponseEntity.ok(aiService.checkDuplicate(request));
    }
}
