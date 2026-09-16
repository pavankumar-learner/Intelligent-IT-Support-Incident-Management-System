package com.example.itsupport.repository;

import com.example.itsupport.entity.Incident;
import com.example.itsupport.entity.IncidentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentHistoryRepository extends JpaRepository<IncidentHistory, Long> {
    List<IncidentHistory> findByIncidentOrderByChangedAtAsc(Incident incident);
}
