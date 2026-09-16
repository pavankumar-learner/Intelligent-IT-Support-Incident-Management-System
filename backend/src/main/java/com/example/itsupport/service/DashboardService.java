package com.example.itsupport.service;

import com.example.itsupport.dto.DashboardStatsDto;
import com.example.itsupport.dto.DistributionDto;
import com.example.itsupport.entity.Priority;
import com.example.itsupport.entity.Status;
import com.example.itsupport.entity.Incident;
import com.example.itsupport.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncidentRepository incidentRepository;

    public DashboardStatsDto getDashboardStatistics() {
        long total = incidentRepository.count();
        long newCount = incidentRepository.countByStatus(Status.NEW);
        long assigned = incidentRepository.countByStatus(Status.ASSIGNED);
        long inProgress = incidentRepository.countByStatus(Status.IN_PROGRESS);
        long resolved = incidentRepository.countByStatus(Status.RESOLVED);
        long closed = incidentRepository.countByStatus(Status.CLOSED);
        long critical = incidentRepository.countByPriority(Priority.CRITICAL);

        // Calculate Average Resolution Time
        List<Incident> resolvedIncidents = incidentRepository.findAll().stream()
                .filter(i -> i.getResolvedAt() != null)
                .collect(Collectors.toList());

        double avgHours = 0.0;
        if (!resolvedIncidents.isEmpty()) {
            double totalHours = resolvedIncidents.stream()
                    .mapToDouble(i -> Duration.between(i.getCreatedAt(), i.getResolvedAt()).toMinutes() / 60.0)
                    .sum();
            avgHours = Math.round((totalHours / resolvedIncidents.size()) * 10.0) / 10.0;
        }

        // Distribution breakdowns
        List<DistributionDto> categoryDist = mapToDistribution(incidentRepository.countByCategoryGroup());
        List<DistributionDto> priorityDist = mapToDistribution(incidentRepository.countByPriorityGroup());
        List<DistributionDto> deptDist = mapToDistribution(incidentRepository.countByDepartmentGroup());
        List<DistributionDto> agentDist = mapToDistribution(incidentRepository.countByAgentWorkloadGroup());

        return DashboardStatsDto.builder()
                .totalIncidents(total)
                .newIncidents(newCount)
                .assignedIncidents(assigned)
                .inProgressIncidents(inProgress)
                .resolvedIncidents(resolved)
                .closedIncidents(closed)
                .criticalIncidents(critical)
                .avgResolutionHours(avgHours)
                .categoryDistribution(categoryDist)
                .priorityDistribution(priorityDist)
                .departmentDistribution(deptDist)
                .agentWorkload(agentDist)
                .build();
    }

    private List<DistributionDto> mapToDistribution(List<Object[]> queryResults) {
        List<DistributionDto> result = new ArrayList<>();
        if (queryResults != null) {
            for (Object[] row : queryResults) {
                String label = row[0] != null ? row[0].toString() : "Unassigned";
                long count = ((Number) row[1]).longValue();
                result.add(new DistributionDto(label, count));
            }
        }
        return result;
    }
}
