package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ExistenceException;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.dao.ReportDAO;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.EReportStatus;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.enumeration.EUserReportType;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.project.ProjectReportData;
import com.fstart.service.model.report.*;
import com.fstart.service.model.user.UserReportData;
import com.fstart.service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/04/14
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final Message message;

    private final UserRepository userRepository;
    private final ViolationRepository violationRepository;
    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final ReportDAO reportDAO;
    private final UserReportRepository userReportRepository;
    private final ProjectReportRepository projectReportRepository;

    public ReportServiceImpl(final Message message,
                             final UserRepository userRepository,
                             final ViolationRepository violationRepository,
                             final ReportRepository reportRepository,
                             final ProjectRepository projectRepository,
                             final ReportDAO reportDAO,
                             final UserReportRepository userReportRepository,
                             final ProjectReportRepository projectReportRepository) {
        this.message = message;
        this.userRepository = userRepository;
        this.violationRepository = violationRepository;
        this.reportRepository = reportRepository;
        this.projectRepository = projectRepository;
        this.reportDAO = reportDAO;
        this.userReportRepository = userReportRepository;
        this.projectReportRepository = projectReportRepository;
    }

    @Override
    public DataWrapper reportUser(final String accuserId, final String reportedId, final Long violationId, final String reason) {
        User accuser = userRepository.findById(accuserId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User reported = userRepository.findById(reportedId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        Violation violation = violationRepository.findById(violationId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        String newestReportDate = reportRepository.newestReportUserDateTime(EUserReportType.ACCUSER.name(), EUserReportType.ACCUSED_USER.name(), accuserId, reportedId);
        if (StringUtils.hasLength(newestReportDate)) {
            LocalDateTime localDateTime = TimeUtils.getDateTime(newestReportDate, TimeUtils.DTF_yyyyMMddHHmmss);
            LocalDateTime currentTime = LocalDateTime.now();
            if (currentTime.compareTo(localDateTime.plusDays(1)) != 1) {
                throw new ServerErrorException(message.getReportSpam());
            }
        }
        Report report = Report.builder()
                .violation(violation)
                .status(EReportStatus.NEW)
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .build();
        // check user choose Something Else
        if (violation.getName().equals("Something Else")) {
            if (StringUtils.hasText(reason)) {
                report.setReason(reason);
            } else {
                throw new ExistenceException(message.getWarnNoData());
            }
        }
        reportRepository.saveAndFlush(report);
        UserReport accuserReport = UserReport.builder()
                .report(report)
                .user(accuser)
                .type(EUserReportType.ACCUSER)
                .build();
        userReportRepository.saveAndFlush(accuserReport);
        UserReport reportedUser = UserReport.builder()
                .report(report)
                .user(reported)
                .type(EUserReportType.ACCUSED_USER)
                .build();
        userReportRepository.saveAndFlush(reportedUser);

        return DataWrapper.builder()
                .data(report.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public DataWrapper reportProject(final String accuserId, final String projectId, final Long violationId, final String reason) {
        User accuser = userRepository.findById(accuserId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        Violation violation = violationRepository.findById(violationId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        String newestReportDate = reportRepository.newestReportProjectDateTime(accuserId, projectId);
        if (StringUtils.hasLength(newestReportDate)) {
            LocalDateTime localDateTime = TimeUtils.getDateTime(newestReportDate, TimeUtils.DTF_yyyyMMddHHmmss);
            LocalDateTime currentTime = LocalDateTime.now();
            if (currentTime.compareTo(localDateTime.plusDays(1)) != 1) {
                throw new ServerErrorException(message.getReportSpam());
            }
        }
        Report report = Report.builder()
                .violation(violation)
                .status(EReportStatus.NEW)
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .build();
        // check user choose Something Else
        if (violationId == Long.parseLong("6")) {
            if (StringUtils.hasText(reason)) {
                report.setReason(reason);
            } else {
                throw new ExistenceException(message.getWarnNoData());
            }
        }
        reportRepository.saveAndFlush(report);
        ProjectReport projectReport = ProjectReport.builder()
                .report(report)
                .project(project)
                .user(accuser)
                .build();
        projectReportRepository.saveAndFlush(projectReport);
        return DataWrapper.builder()
                .data(report.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<ReportProjectData> getAllProjectReportBy(final String role, final String status, final Integer page, final Integer size, final String search) {

        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        List<ReportProjectData> reportProjectData = reportDAO.getAllProjectReportBy(status, size, (page - 1) * size, search);
        long totalElements = reportDAO.countAllProjectReportBy(status, search);
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));

        return new PagedResponse<>(reportProjectData, page, size, totalElements, totalPages);
    }

    @Override
    public PagedResponse<ReportUserData> getAllUserReportBy(final String role, final String status, final Integer page, final Integer size, final String search) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        List<ReportUserData> reportUserData = reportDAO.getAllUserReportBy(status, EUserReportType.ACCUSED_USER, size, (page - 1) * size, search);
        long totalElements = reportDAO.countAllUserReportBy(status, EUserReportType.ACCUSED_USER, search);
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));

        return new PagedResponse<>(reportUserData, page, size, totalElements, totalPages);
    }

    @Override
    public List<ViolationReportData> getAllReportByProject(final String projectId, final String role, final String status) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        List<Violation> violations;
        if (StringUtils.hasLength(status)) {
            violations = violationRepository.getAllByProjectIdAndStatus(projectId, EReportStatus.valueOf(status));
        } else {
            violations = violationRepository.getAllByProjectId(projectId);
        }
        List<ViolationReportData> violationReportDataList = violations.stream()
                .map(violation -> {
                    ViolationReportData violationReportData = new ViolationReportData();
                    violationReportData.setViolation(violation.getName());
                    List<Report> reports;
                    if (StringUtils.hasLength(status)) {
                        reports = reportRepository.getAllByViolationAndProjectAndStatus(violation, projectId, EReportStatus.valueOf(status));
                    } else {
                        reports = reportRepository.getAllByViolationAndProject(violation, projectId);
                    }
                    List<ReportData> reportDataList = DataBuilder.toList(reports, ReportData.class);
                    violationReportData.setReportDataList(reportDataList);
                    return violationReportData;
                }).collect(Collectors.toList());

        return violationReportDataList;
    }

    @Override
    public CommonReportData countReportByProject(final String roleId, final String projectId) {
        if (!ERole.ADMIN.name().equals(roleId)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        CommonReportData commonReportData = CommonReportData.builder()
                .numberOfNewReport(reportRepository.countAllByProjectAndStatus(projectId, EReportStatus.NEW))
                .numberOfAcceptedReport(reportRepository.countAllByProjectAndStatus(projectId, EReportStatus.ACCEPTED))
                .numberOfRejectedReport(reportRepository.countAllByProjectAndStatus(projectId, EReportStatus.REJECTED))
                .build();

        return commonReportData;
    }

    @Override
    public CommonReportData countReportByUser(final String roleId, final String accusedUserId) {
        if (!ERole.ADMIN.name().equals(roleId)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        CommonReportData commonReportData = CommonReportData.builder()
                .numberOfNewReport(reportRepository.countAllByUserAndStatusAndType(accusedUserId, EReportStatus.NEW, EUserReportType.ACCUSED_USER))
                .numberOfAcceptedReport(reportRepository.countAllByUserAndStatusAndType(accusedUserId, EReportStatus.ACCEPTED, EUserReportType.ACCUSED_USER))
                .numberOfRejectedReport(reportRepository.countAllByUserAndStatusAndType(accusedUserId, EReportStatus.REJECTED, EUserReportType.ACCUSED_USER))
                .build();

        return commonReportData;
    }

    @Override
    public List<ViolationReportData> getAllReportByUser(final String accusedUserId, final String role, final String status) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        List<Violation> violations;

        if (StringUtils.hasLength(status)) {
            violations = violationRepository.getAllByUserIdAndStatus(accusedUserId, EReportStatus.valueOf(status), EUserReportType.ACCUSED_USER);

        } else {
            violations = violationRepository.getAllByUserId(accusedUserId, EUserReportType.ACCUSED_USER);
        }
        List<ViolationReportData> violationReportDataList = violations.stream()
                .map(violation -> {
                    ViolationReportData violationReportData = new ViolationReportData();
                    violationReportData.setViolation(violation.getName());
                    List<Report> reports;
                    if (StringUtils.hasLength(status)) {
                        reports = reportRepository.getAllByViolationAndUserAndStatus(accusedUserId, EReportStatus.valueOf(status), violation, EUserReportType.ACCUSED_USER);
                    } else {
                        reports = reportRepository.getAllByViolationAndUser(accusedUserId, violation, EUserReportType.ACCUSED_USER);
                    }
                    List<ReportData> reportDataList = DataBuilder.toList(reports, ReportData.class);
                    violationReportData.setReportDataList(reportDataList);
                    return violationReportData;
                }).collect(Collectors.toList());

        return violationReportDataList;
    }

    @Override
    public DataWrapper acceptReport(final Long reportId, final String role) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        report.setStatus(EReportStatus.ACCEPTED);
        reportRepository.saveAndFlush(report);
        return DataWrapper.builder()
                .data(report.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public DataWrapper rejectReport(final Long reportId, final String role) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        report.setStatus(EReportStatus.REJECTED);
        reportRepository.saveAndFlush(report);
        return DataWrapper.builder()
                .data(report.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public List<ReportUserData> getMyUserReport(final String userId) {
        User user = userRepository.getById(userId);
        List<UserReport> userReports = userReportRepository.findAllByUserAndType(user, EUserReportType.ACCUSER);
        if (CollectionUtils.isEmpty(userReports)) {
            return null;
        }

        List<UserReport> userReportedList = userReports.stream()
                .map(userReport -> {
                    UserReport userReported = userReportRepository.findByReportAndType(userReport.getReport(), EUserReportType.ACCUSED_USER);
                    return userReported;
                }).collect(Collectors.toList());

        List<ReportUserData> reportUserDataList = userReportedList.stream()
                .map(userReport -> {
                    ReportUserData reportUserData = ReportUserData.builder()
                            .reportId(userReport.getReport().getId())
                            .reason(userReport.getReport().getReason())
                            .violation(userReport.getReport().getViolation().getName())
                            .status(userReport.getReport().getStatus())
                            .accusedId(userReport.getUser().getId())
                            .avatar(userReport.getUser().getAvatar())
                            .createdAt(userReport.getReport().getCreatedAt())
                            .updatedAt(userReport.getReport().getUpdatedAt())
                            .firstName(userReport.getUser().getFirstName())
                            .lastName(userReport.getUser().getLastName())
                            .build();
                    return reportUserData;
                }).collect(Collectors.toList());

        return reportUserDataList;
    }

    @Override
    public List<ReportProjectData> getMyProjectReport(final String userId) {
        User user = userRepository.getById(userId);
        List<ProjectReport> projectReports = projectReportRepository.findAllByUser(user);
        if (CollectionUtils.isEmpty(projectReports)) {
            return null;
        }
        List<ReportProjectData> reportProjectDataList = projectReports.stream()
                .map(projectReport -> {
                    ReportProjectData reportProjectData = ReportProjectData.builder()
                            .projectId(projectReport.getProject().getId())
                            .logo(projectReport.getProject().getLogo())
                            .projectTitle(projectReport.getProject().getTitle())
                            .reportId(projectReport.getReport().getId())
                            .createdAt(projectReport.getReport().getCreatedAt())
                            .updatedAt(projectReport.getReport().getUpdatedAt())
                            .reason(projectReport.getReport().getReason())
                            .status(projectReport.getReport().getStatus())
                            .violation(projectReport.getReport().getViolation().getName())
                            .build();
                    return reportProjectData;
                }).collect(Collectors.toList());
        return reportProjectDataList;
    }
}
