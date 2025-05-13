package com.fstart.service.repository;

import com.fstart.service.entity.Report;
import com.fstart.service.entity.User;
import com.fstart.service.entity.UserReport;
import com.fstart.service.enumeration.EUserReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserReportRepository
 *
 * @author: VuongVT2
 * @since: 2022/06/01
 */
@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    Long countAllByTypeAndUser(EUserReportType type, User user);

    List<UserReport> findAllByUserAndType(User user, EUserReportType type);

    @Query("select fur from UserReport fur where fur.report = :report and fur.type = :type " +
            "order by fur.report.createdAt desc ")
    UserReport findByReportAndType(@Param("report") Report report,
                                   @Param("type") EUserReportType type);

}
