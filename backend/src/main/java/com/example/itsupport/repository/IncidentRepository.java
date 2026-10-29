package com.example.itsupport.repository;

import com.example.itsupport.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByCreatedBy(User createdBy);

    List<Incident> findByAssignedTo(User assignedTo);

    List<Incident> findTop100ByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.status = :status")
    long countByStatus(@Param("status") Status status);

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.priority = :priority")
    long countByPriority(@Param("priority") Priority priority);

    @Query("SELECT i.category, COUNT(i) FROM Incident i GROUP BY i.category")
    List<Object[]> countByCategoryGroup();

    @Query("SELECT i.priority, COUNT(i) FROM Incident i GROUP BY i.priority")
    List<Object[]> countByPriorityGroup();

    @Query("SELECT i.department, COUNT(i) FROM Incident i GROUP BY i.department")
    List<Object[]> countByDepartmentGroup();

    @Query("SELECT i.assignedTo.name, COUNT(i) FROM Incident i WHERE i.assignedTo IS NOT NULL GROUP BY i.assignedTo.name")
    List<Object[]> countByAgentWorkloadGroup();
}

