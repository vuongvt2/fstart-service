package com.fstart.service.repository;

import com.fstart.service.entity.Project;
import com.fstart.service.entity.ProjectReport;
import com.fstart.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProjectReportRepository
 *
 * @author: VuongVT2
 * @since: 2022/06/01
 */
@Repository
public interface ProjectReportRepository extends JpaRepository<ProjectReport, Long> {
    Long countAllByProject(Project project);

    void deleteAllByProject(Project project);

    List<ProjectReport> findAllByProject(Project project);

    @Query("select fpr from ProjectReport fpr " +
            "where fpr.user = :user order by fpr.report.createdAt desc ")
    List<ProjectReport> findAllByUser(@Param("user") User user);
}
