package com.fstart.service.repository;

import com.fstart.service.entity.Project;
import com.fstart.service.entity.ProjectTechnology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProjectTechnologyRepository
 *
 * @author: VuongVT2
 * @since: 2021/11/13
 */
@Repository
public interface ProjectTechnologyRepository extends JpaRepository<ProjectTechnology, Long> {

    List<ProjectTechnology> findByProject(Project project);

    void deleteAllByProject(Project project);

    void deleteAllByTechnologyIdIn(List<String> technologiesId);

}
