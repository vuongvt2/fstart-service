package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.project.ProjectReportData;
import com.fstart.service.model.report.CommonReportData;
import com.fstart.service.model.report.ReportProjectData;
import com.fstart.service.model.report.ReportUserData;
import com.fstart.service.model.report.ViolationReportData;
import com.fstart.service.model.user.UserReportData;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ReportController
 *
 * @author: VuongVT2
 * @since: 2022/04/14
 */
@RestController
@RequestMapping(value = "/fs/api/v1/report")
public class ReportController {

    private final AccessTokenService accessTokenService;
    private final ReportService reportService;


    public ReportController(final AccessTokenService accessTokenService,
                            final ReportService reportService) {
        this.accessTokenService = accessTokenService;
        this.reportService = reportService;
    }

    @GetMapping(value = "/report-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper reportUser(HttpServletRequest request,
                                  @RequestParam(value = "reportedId") @NotBlank String reportedId,
                                  @RequestParam(value = "violationId") @NotNull Long violationId,
                                  @RequestParam(value = "reason", defaultValue = AppConstant.DEFAULT_STR_VALUE) String reason) {
        String accuserId = accessTokenService.getUserID(request);
        return reportService.reportUser(accuserId, reportedId, violationId, reason);
    }

    @GetMapping(value = "/report-project", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper reportProject(HttpServletRequest request,
                                     @RequestParam(value = "projectId") @NotBlank String projectId,
                                     @RequestParam(value = "violationId") @NotNull Long violationId,
                                     @RequestParam(value = "reason", defaultValue = AppConstant.DEFAULT_STR_VALUE) String reason) {
        String accuserId = accessTokenService.getUserID(request);
        return reportService.reportProject(accuserId, projectId, violationId, reason);
    }

    @GetMapping(value = "/list-project-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<ReportProjectData> getAllProjectReportBy(
            @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
            @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
            HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return reportService.getAllProjectReportBy(role, status, page, size, search);
    }

    @GetMapping(value = "/list-user-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<ReportUserData> getAllUserReportBy(
            @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
            @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
            @RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
            HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return reportService.getAllUserReportBy(role, status, page, size, search);
    }

    @GetMapping(value = "/list-report-by-project", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ViolationReportData> getAllReportByProject(
            @RequestParam(name = "projectId") @NotBlank String projectId,
            @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
            HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return reportService.getAllReportByProject(projectId, role, status);
    }

    @GetMapping(value = "/list-report-by-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ViolationReportData> getAllReportByUser(
            @RequestParam(name = "accusedUserId") @NotBlank String accusedUserId,
            @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
            HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return reportService.getAllReportByUser(accusedUserId, role, status);
    }

    @GetMapping(value = "/common-data/project", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonReportData countReportByProject(@RequestParam(name = "projectId") @NotBlank String projectId,
                                                 HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        return reportService.countReportByProject(roleId, projectId);
    }

    @GetMapping(value = "/common-data/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonReportData countReportByUser(@RequestParam(name = "accusedUserId") @NotBlank String accusedUserId,
                                              HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        return reportService.countReportByUser(roleId, accusedUserId);
    }

    @GetMapping(value = "/accept-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper acceptReport(@RequestParam(name = "reportId") Long reportId,
                                    HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return reportService.acceptReport(reportId, role);
    }

    @GetMapping(value = "/reject-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper rejectReport(@RequestParam(name = "reportId") Long reportId,
                                    HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return reportService.rejectReport(reportId, role);
    }

    @GetMapping(value = "/my-user-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReportUserData> getMyUserReport(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return reportService.getMyUserReport(userId);
    }

    @GetMapping(value = "/my-project-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReportProjectData> getMyProjectReport(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return reportService.getMyProjectReport(userId);
    }

}
