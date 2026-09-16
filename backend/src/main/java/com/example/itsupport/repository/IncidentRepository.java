package com.example.itsupport.repository;

import com.example.itsupport.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {

    Optional<Incident> findByTicketNumber(String ticketNumber);

    List<Incident> findByCreatedBy(User createdBy);
    
    Page<Incident> findByCreatedBy(User createdBy, Pageable pageable);

    List<Incident> findByAssignedTo(User assignedTo);

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

    @Query("SELECT i FROM Incident i ORDER BY i.createdAt DESC")
    List<Incident> findTop10Recent();
}
