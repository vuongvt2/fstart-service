package com.fstart.service.repository;

import com.fstart.service.entity.Field;
import com.fstart.service.entity.Position;
import com.fstart.service.entity.Project;
import com.fstart.service.entity.Technology;
import com.fstart.service.enumeration.EEventParticipantStatus;
import com.fstart.service.enumeration.EProjectStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {

    List<Project> findAllByStatusOrderByUpdatedAtDesc(EProjectStatus status, Pageable pageable);

    @Query(value = "SELECT distinct fp" +
            "       FROM Project fp " +
            "       LEFT JOIN ProjectPosition fpp " +
            "       ON fp.id = fpp.project.id" +
            "       WHERE fpp.position" +
            "       IN :positions")
    List<Project> getProjectsByPosition(@Param("positions") List<Position> positions);


    @Query(value = "SELECT distinct fp " +
            "       FROM Project fp " +
            "       LEFT JOIN ProjectField ff " +
            "       ON fp.id = ff.project.id" +
            "       WHERE ff.field" +
            "       IN :fields")
    List<Project> getProjectsByField(@Param("fields") List<Field> fields);

    @Query(value = "SELECT distinct fp" +
            "       FROM Project fp " +
            "       LEFT JOIN ProjectTechnology ft " +
            "       ON fp.id = ft.project.id" +
            "       WHERE ft.technology" +
            "       IN :technologies")
    List<Project> getProjectsBySkill(@Param("technologies") List<Technology> technologies);

    @Query("SELECT  distinct fp FROM Project fp " +
            " LEFT JOIN EventParticipant fep ON fp.id = fep.project.id where fep.event.id = :eventId and fep.status = :status")
    List<Project> findProjectsByEventAndStatus(@Param("eventId") String eventId, EEventParticipantStatus status);

    @Query("SELECT count (distinct p.id) FROM Project p ")
    Long countAll();

    Long countAllByStatus(EProjectStatus status);
}