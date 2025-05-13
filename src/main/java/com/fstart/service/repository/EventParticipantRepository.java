package com.fstart.service.repository;

import com.fstart.service.entity.Event;
import com.fstart.service.entity.EventParticipant;
import com.fstart.service.entity.Project;
import com.fstart.service.enumeration.EEventParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    @Transactional(readOnly = true)
    @Query("SELECT fep " +
            "FROM EventParticipant fep " +
            "LEFT JOIN Event fe ON fep.event.id = fe.id " +
            "WHERE fep.status = :status " +
            "AND unaccent(lower(fe.title)) like unaccent(lower(concat('%', :search ,'%' ))) " +
            "ORDER BY fe.updatedAt" )
    Page<EventParticipant> findAllJoinEventRequest(Pageable pageable, String search, EEventParticipantStatus status);

    @Query("SELECT n FROM EventParticipant n where n.event.id = :eventId and n.status = :status")
    List<EventParticipant> findEventParticipantsByEventAndStatus(@Param("eventId") String eventId, EEventParticipantStatus status);

    void deleteEventParticipantsByEvent(Event event);

    EventParticipant findEventParticipantByEventAndAndProject(Event event, Project project);

    //    @Query("SELECT n FROM EventParticipant n where n.event.id = :eventId and n.status = :status")
//    EventParticipant findEventParticipantByProjectAndEvent(Project project, Event event);
    EventParticipant findByProjectAndEvent(Project project, Event event);

    void deleteEventParticipantsByEventAndProject(Event event, Project project);

    void deleteAllByProject(Project project);

    List<EventParticipant> findAllByProject(Project project);

    boolean existsByProjectAndAndEvent(Project project, Event event);
}
