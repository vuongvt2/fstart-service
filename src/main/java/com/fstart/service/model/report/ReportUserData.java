package com.fstart.service.model.report;

import com.fstart.service.enumeration.EReportStatus;
import lombok.*;

/**
 * ReportUserData
 *
 * @author: VuongVT2
 * @since: 2022/05/12
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportUserData {
    private Long reportId;
    private String reason;
    private EReportStatus status;
    private String violation;
    private String createdAt;
    private String updatedAt;
    private String accusedId;
    private String avatar;
    private String firstName;
    private String lastName;

    public ReportUserData(final Long reportId, final String reason, final EReportStatus status, final String violation, final String createdAt, final String updatedAt) {
        this.reportId = reportId;
        this.reason = reason;
        this.status = status;
        this.violation = violation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
