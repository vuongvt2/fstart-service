package com.fstart.service.dao;

import com.fstart.service.enumeration.EUserReportType;
import com.fstart.service.model.report.ReportProjectData;
import com.fstart.service.model.report.ReportUserData;

import java.util.List;

/**
 * ReportDAO
 *
 * @author: VuongVT2
 * @since: 2022/05/11
 */
public interface ReportDAO {
    List<ReportProjectData> getAllProjectReportBy(String status, long limit, long offset, String search);

    Long countAllProjectReportBy(String status, String search);

    List<ReportUserData> getAllUserReportBy(String status, EUserReportType type, long limit, long offset, String search);

    Long countAllUserReportBy(String status, EUserReportType type, String search);
}
