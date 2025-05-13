package com.fstart.service.repository;

import com.fstart.service.entity.Violation;
import com.fstart.service.enumeration.EReportStatus;
import com.fstart.service.enumeration.EUserReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ViolationRepository
 *
 * @author: VuongVT2
 * @since: 2022/04/14
 */
@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    @Query("select distinct(fv) from Violation fv left join Report fr on fv.id = fr.violation.id " +
            "left join UserReport fupr on fr.id = fupr.report.id " +
            "where fupr.user.id = :userId and fupr.type = :type and fr.status = :status")
    List<Violation> getAllByUserIdAndStatus(@Param("userId") String userId,
                                            @Param("status") EReportStatus status,
                                            @Param("type") EUserReportType type);

    @Query("select distinct(fv) from Violation fv left join Report fr on fv.id = fr.violation.id " +
            "left join UserReport fupr on fr.id = fupr.report.id " +
            "where fupr.user.id = :userId and fupr.type = :type ")
    List<Violation> getAllByUserId(@Param("userId") String userId,
                                   @Param("type") EUserReportType type);

    @Query("select distinct(fv) from Violation fv left join Report fr on fv.id = fr.violation.id " +
            "left join ProjectReport fupr on fr.id = fupr.report.id " +
            "where fupr.project.id = :projectId and fr.status = :status")
    List<Violation> getAllByProjectIdAndStatus(@Param("projectId") String projectId,
                                               @Param("status") EReportStatus status);

    @Query("select distinct(fv) from Violation fv left join Report fr on fv.id = fr.violation.id " +
            "left join ProjectReport fupr on fr.id = fupr.report.id " +
            "where fupr.project.id = :projectId ")
    List<Violation> getAllByProjectId(@Param("projectId") String projectId);
}
