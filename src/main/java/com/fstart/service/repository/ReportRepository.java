package com.fstart.service.repository;

import com.fstart.service.entity.Report;
import com.fstart.service.entity.Violation;
import com.fstart.service.enumeration.EReportStatus;
import com.fstart.service.enumeration.EUserReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ReportRepository
 *
 * @author: VuongVT2
 * @since: 2022/04/14
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "select fr.created_at from fs_report fr " +
            "    inner join " +
            "       fs_user_report fupr on fr.id = fupr.report_id " +
            "    where " +
            "       fupr.type = :accusedUserType " +
            "       AND fupr.user_id = :reportedId " +
            "       AND fr.id IN (select fr.id " +
            "           from fs_report fr " +
            "           inner join " +
            "           fs_user_report fupr on fr.id = fupr.report_id " +
            "           where user_id = :accuserId " +
            "           and fupr.type = :accuserType) " +
            "order by fr.created_at desc limit 1 ", nativeQuery = true)
    String newestReportUserDateTime(@Param("accuserType") String accuserType,
                                    @Param("accusedUserType") String accusedUserType,
                                    @Param("accuserId") String accuserId,
                                    @Param("reportedId") String reportedId);

    @Query(value = "select fr.created_at " +
            "       from fs_report fr " +
            "       inner join fs_project_report fupr " +
            "           on fr.id = fupr.report_id " +
            "       where fupr.user_id = :accuserId " +
            "               and fupr.project_id = :projectId " +
            "       order by fr.created_at desc limit 1 ", nativeQuery = true)
    String newestReportProjectDateTime(@Param("accuserId") String accuserId,
                                       @Param("projectId") String projectId);

    @Query("select count(distinct fr.id) from Report fr " +
            "left join ProjectReport fupr on fr.id = fupr.report.id " +
            "where fupr.project.id = :projectId and fr.status = :status")
    Long countAllByProjectAndStatus(@Param("projectId") String projectId,
                                    @Param("status") EReportStatus status);

    @Query("select count(distinct fr.id) from Report fr " +
            "left join UserReport fupr on fr.id = fupr.report.id " +
            "where fupr.type = :type " +
            "   and fr.status = :status " +
            "   and fupr.user.id = :accusedUserId ")
    Long countAllByUserAndStatusAndType(@Param("accusedUserId") String accusedUserId,
                                        @Param("status") EReportStatus status,
                                        @Param("type") EUserReportType type);

    @Query("select fr from Report fr " +
            "left join UserReport fupr on fr.id = fupr.report.id " +
            "where fupr.type = :type " +
            "   and fr.status = :status " +
            "   and fr.violation = :violation " +
            "   and fupr.user.id = :accusedUserId ")
    List<Report> getAllByViolationAndUserAndStatus(@Param("accusedUserId") String accusedUserId,
                                                   @Param("status") EReportStatus status,
                                                   @Param("violation") Violation violation,
                                                   @Param("type") EUserReportType type);

    @Query("select fr from Report fr " +
            "left join UserReport fupr on fr.id = fupr.report.id " +
            "where fupr.type = :type " +
            "   and fr.violation = :violation " +
            "   and fupr.user.id = :accusedUserId ")
    List<Report> getAllByViolationAndUser(@Param("accusedUserId") String accusedUserId,
                                          @Param("violation") Violation violation,
                                          @Param("type") EUserReportType type);

    @Query("select fr from Report fr " +
            "left join ProjectReport fupr on fr.id = fupr.report.id " +
            "where fupr.project.id = :projectId and fr.status = :status and fr.violation = :violation")
    List<Report> getAllByViolationAndProjectAndStatus(@Param("violation") Violation violation,
                                                      @Param("projectId") String projectId,
                                                      @Param("status") EReportStatus status);

    @Query("select fr from Report fr " +
            "left join ProjectReport fupr on fr.id = fupr.report.id " +
            "where fupr.project.id = :projectId  and fr.violation = :violation")
    List<Report> getAllByViolationAndProject(@Param("violation") Violation violation,
                                             @Param("projectId") String projectId);

    @Query("SELECT count (distinct r.id) FROM Report r")
    Long countAll();

    Long countAllByStatus(EReportStatus status);
}
