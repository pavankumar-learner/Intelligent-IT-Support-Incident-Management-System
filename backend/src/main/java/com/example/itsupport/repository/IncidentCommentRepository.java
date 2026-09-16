package com.example.itsupport.repository;

import com.example.itsupport.entity.Incident;
import com.example.itsupport.entity.IncidentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentCommentRepository extends JpaRepository<IncidentComment, Long> {
    List<IncidentComment> findByIncidentOrderByCreatedAtAsc(Incident incident);
}
