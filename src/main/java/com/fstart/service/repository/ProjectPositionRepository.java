package com.fstart.service.repository;

import com.fstart.service.entity.Position;
import com.fstart.service.entity.Project;
import com.fstart.service.entity.ProjectPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProjectPositionRepository
 *
 * @author: VuongVT2
 * @since: 2022/05/16
 */
@Repository
public interface ProjectPositionRepository extends JpaRepository<ProjectPosition, Long> {

    List<ProjectPosition> findAllByProject(Project project);

    boolean existsByPositionAndProject(Position position, Project project);

    ProjectPosition findByPositionAndProject(Position position, Project project);

    void deleteAllByProject(Project project);

    int countAllByProject(Project project);

    @Query("select fpp.availableSlot from ProjectPosition fpp where fpp.project = :project")
    List<Long> getListAvailableSlotProject(@Param("project") Project project);
}
