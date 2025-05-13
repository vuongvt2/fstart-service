package com.fstart.service.repository;

import com.fstart.service.entity.Document;
import com.fstart.service.entity.EventParticipant;
import com.fstart.service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByProject(Project project);

    void deleteAllByProject(Project project);

    void deleteAllByEventParticipant(EventParticipant eventParticipant);
}