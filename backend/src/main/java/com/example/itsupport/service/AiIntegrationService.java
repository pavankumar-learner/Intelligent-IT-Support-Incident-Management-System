package com.example.itsupport.service;

import com.example.itsupport.dto.*;
import com.example.itsupport.entity.Category;
import com.example.itsupport.entity.Priority;
import com.example.itsupport.entity.Incident;
import com.example.itsupport.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiIntegrationService {

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    private final IncidentRepository incidentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public AiPredictionResponse predictCategory(AiPredictionRequest request) {
        try {
            String url = mlServiceUrl + "/predict-category";
            Map<String, String> body = Map.of(
                    "title", request.getTitle() != null ? request.getTitle() : "",
                    "description", request.getDescription() != null ? request.getDescription() : ""
            );
            ResponseEntity<AiPredictionResponse> response = restTemplate.postForEntity(url, body, AiPredictionResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.warn("FastAPI ML service unreachable at {}. Using Java NLP heuristic fallback for category.", mlServiceUrl);
        }

        // Fallback Java Heuristic Category Classifier
        String text = (request.getTitle() + " " + (request.getDescription() != null ? request.getDescription() : "")).toLowerCase();
        Category cat = fallbackCategory(text);
        List<String> keywords = extractKeywords(text);

        return AiPredictionResponse.builder()
                .prediction(cat.toDisplayString())
                .confidence(0.85)
                .topKeywords(keywords)
                .build();
    }

    public AiPredictionResponse predictPriority(AiPredictionRequest request) {
        try {
            String url = mlServiceUrl + "/predict-priority";
            Map<String, String> body = Map.of(
                    "title", request.getTitle() != null ? request.getTitle() : "",
                    "description", request.getDescription() != null ? request.getDescription() : "",
                    "category", request.getCategory() != null ? request.getCategory() : ""
            );
            ResponseEntity<AiPredictionResponse> response = restTemplate.postForEntity(url, body, AiPredictionResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.warn("FastAPI ML service unreachable at {}. Using Java NLP heuristic fallback for priority.", mlServiceUrl);
        }

        // Fallback Java Heuristic Priority Estimator
        String text = (request.getTitle() + " " + (request.getDescription() != null ? request.getDescription() : "")).toLowerCase();
        Priority pri = fallbackPriority(text);
        List<String> keywords = extractKeywords(text);

        return AiPredictionResponse.builder()
                .prediction(pri.name())
                .confidence(0.80)
                .topKeywords(keywords)
                .build();
    }

    public DuplicateCheckResponse checkDuplicate(DuplicateCheckRequest request) {
        List<Incident> existingIncidents = incidentRepository.findAll();

        List<Map<String, String>> candidateList = new ArrayList<>();
        for (Incident inc : existingIncidents) {
            candidateList.add(Map.of(
                    "ticketNumber", inc.getTicketNumber(),
                    "title", inc.getTitle(),
                    "description", inc.getDescription() != null ? inc.getDescription() : ""
            ));
        }

        try {
            String url = mlServiceUrl + "/check-duplicate";
            Map<String, Object> body = Map.of(
                    "title", request.getTitle() != null ? request.getTitle() : "",
                    "description", request.getDescription() != null ? request.getDescription() : "",
                    "threshold", request.getThreshold() != null ? request.getThreshold() : 0.65,
                    "existingTickets", candidateList
            );
            ResponseEntity<DuplicateCheckResponse> response = restTemplate.postForEntity(url, body, DuplicateCheckResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.warn("FastAPI ML service unreachable. Performing Java Jaccard/N-gram text similarity fallback.");
        }

        // Java Fallback Similarity Matcher
        String queryText = (request.getTitle() + " " + (request.getDescription() != null ? request.getDescription() : "")).toLowerCase();
        double highestSim = 0.0;
        String matchedTicketNo = null;
        String matchedTitle = null;

        for (Incident inc : existingIncidents) {
            String targetText = (inc.getTitle() + " " + (inc.getDescription() != null ? inc.getDescription() : "")).toLowerCase();
            double sim = calculateJaccardSimilarity(queryText, targetText);
            if (sim > highestSim) {
                highestSim = sim;
                matchedTicketNo = inc.getTicketNumber();
                matchedTitle = inc.getTitle();
            }
        }

        double threshold = request.getThreshold() != null ? request.getThreshold() : 0.65;
        boolean isDup = highestSim >= threshold;

        return DuplicateCheckResponse.builder()
                .isDuplicate(isDup)
                .similarityScore(Math.round(highestSim * 1000.0) / 10.0)
                .matchedTicketNumber(matchedTicketNo)
                .matchedTitle(matchedTitle)
                .message(isDup ? String.format("Potential duplicate incident detected (%.1f%% similarity with %s).", highestSim * 100, matchedTicketNo)
                               : "No duplicate incidents detected.")
                .build();
    }

    private Category fallbackCategory(String text) {
        if (text.contains("wifi") || text.contains("network") || text.contains("vpn") || text.contains("router") || text.contains("internet"))
            return Category.Network;
        if (text.contains("printer") || text.contains("toner") || text.contains("paper") || text.contains("spooler"))
            return Category.Printer;
        if (text.contains("login") || text.contains("password") || text.contains("2fa") || text.contains("access") || text.contains("account"))
            return Category.Access_Login;
        if (text.contains("email") || text.contains("outlook") || text.contains("mailbox") || text.contains("inbox"))
            return Category.Email;
        if (text.contains("virus") || text.contains("phishing") || text.contains("firewall") || text.contains("malware") || text.contains("security"))
            return Category.Security;
        if (text.contains("database") || text.contains("sql") || text.contains("mysql") || text.contains("postgres") || text.contains("query"))
            return Category.Database;
        if (text.contains("laptop") || text.contains("monitor") || text.contains("keyboard") || text.contains("mouse") || text.contains("hardware"))
            return Category.Hardware;
        if (text.contains("software") || text.contains("crash") || text.contains("bsod") || text.contains("install") || text.contains("bug"))
            return Category.Software;
        return Category.Other;
    }

    private Priority fallbackPriority(String text) {
        if (text.contains("entire") || text.contains("down") || text.contains("outage") || text.contains("critical") || text.contains("breach") || text.contains("production"))
            return Priority.CRITICAL;
        if (text.contains("unable") || text.contains("broken") || text.contains("failed") || text.contains("cannot") || text.contains("high"))
            return Priority.HIGH;
        if (text.contains("slow") || text.contains("issue") || text.contains("lag"))
            return Priority.MEDIUM;
        return Priority.LOW;
    }

    private List<String> extractKeywords(String text) {
        String[] words = text.replaceAll("[^a-zA-Z0-9 ]", "").split("\\s+");
        Set<String> stopWords = Set.of("the", "and", "is", "not", "to", "for", "with", "this", "my", "in", "on", "a", "an");
        List<String> res = new ArrayList<>();
        for (String w : words) {
            if (w.length() > 3 && !stopWords.contains(w.toLowerCase())) {
                res.add(w);
                if (res.size() >= 3) break;
            }
        }
        return res;
    }

    private double calculateJaccardSimilarity(String s1, String s2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(s1.toLowerCase().split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(s2.toLowerCase().split("\\s+")));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }
}
