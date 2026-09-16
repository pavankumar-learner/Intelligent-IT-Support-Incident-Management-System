package com.example.itsupport.controller;

import com.example.itsupport.dto.*;
import com.example.itsupport.entity.*;
import com.example.itsupport.repository.UserRepository;
import com.example.itsupport.security.UserPrincipal;
import com.example.itsupport.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;
    private final UserRepository userRepository;

    private User getCurrentUserEntity(UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(
            @Valid @RequestBody IncidentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(incidentService.createIncident(request, currentUser.getEmail()));
    }

    @GetMapping
    public ResponseEntity<List<IncidentResponse>> getAllIncidents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String department,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        User user = getCurrentUserEntity(currentUser);
        return ResponseEntity.ok(incidentService.getAllIncidents(search, category, priority, status, department, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        User user = getCurrentUserEntity(currentUser);
        return ResponseEntity.ok(incidentService.getIncidentById(id, user));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<IncidentResponse> assignAgent(
            @PathVariable Long id,
            @Valid @RequestBody AssignRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        User user = getCurrentUserEntity(currentUser);
        return ResponseEntity.ok(incidentService.assignAgent(id, request.getAgentId(), user));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<IncidentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        User user = getCurrentUserEntity(currentUser);
        return ResponseEntity.ok(incidentService.updateStatus(id, request, user));
    }

    @PutMapping("/{id}/priority")
    public ResponseEntity<IncidentResponse> updatePriority(
            @PathVariable Long id,
            @Valid @RequestBody PriorityUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        User user = getCurrentUserEntity(currentUser);
        return ResponseEntity.ok(incidentService.updatePriority(id, request, user));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        User user = getCurrentUserEntity(currentUser);
        return ResponseEntity.ok(incidentService.addComment(id, request.getComment(), user));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getComments(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<IncidentHistoryDto>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getHistory(id));
    }
}
