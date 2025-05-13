package com.fstart.service.repository;

import com.fstart.service.entity.Project;
import com.fstart.service.entity.ProjectField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProjectFieldRepository
 *
 * @author: VuongVT2
 * @since: 2021/11/13
 */
@Repository
public interface ProjectFieldRepository extends JpaRepository<ProjectField, Long> {

    List<ProjectField> findByProject(Project project);

    void deleteAllByProject(Project project);

    void deleteAllByFieldIdIn(List<String> fieldId);

}
