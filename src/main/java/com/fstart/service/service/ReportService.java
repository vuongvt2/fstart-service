package com.fstart.service.service;

import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.project.ProjectReportData;
import com.fstart.service.model.report.CommonReportData;
import com.fstart.service.model.report.ReportProjectData;
import com.fstart.service.model.report.ReportUserData;
import com.fstart.service.model.report.ViolationReportData;
import com.fstart.service.model.user.UserReportData;

import java.util.List;

/**
 * ReportService
 *
 * @author: VuongVT2
 * @since: 2022/04/14
 */
public interface ReportService {
    DataWrapper reportUser(String accuserId, String reportedId, Long violationId, String reason);

    DataWrapper reportProject(String accuserId, String projectId, Long violationId, String reason);

    PagedResponse<ReportProjectData> getAllProjectReportBy(String role, String status, Integer page, Integer size, String search);

    PagedResponse<ReportUserData> getAllUserReportBy(String role, String status, Integer page, Integer size, String search);

    List<ViolationReportData> getAllReportByProject(String projectId, String role, String status);

    CommonReportData countReportByProject(String roleId, String projectId);

    CommonReportData countReportByUser(String roleId, String accusedUserId);

    List<ViolationReportData> getAllReportByUser(String accusedUserId, String role, String status);

    DataWrapper acceptReport(Long reportId, String role);

    DataWrapper rejectReport(Long reportId, String role);

    List<ReportUserData> getMyUserReport(String userId);

    List<ReportProjectData> getMyProjectReport(String userId);
}
