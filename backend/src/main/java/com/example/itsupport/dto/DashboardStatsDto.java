package com.example.itsupport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDto {
    private long totalIncidents;
    private long newIncidents;
    private long assignedIncidents;
    private long inProgressIncidents;
    private long resolvedIncidents;
    private long closedIncidents;
    private long criticalIncidents;
    private double avgResolutionHours;
    private List<DistributionDto> categoryDistribution;
    private List<DistributionDto> priorityDistribution;
    private List<DistributionDto> departmentDistribution;
    private List<DistributionDto> agentWorkload;
}
