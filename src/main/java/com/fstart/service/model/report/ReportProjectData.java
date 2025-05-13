package com.fstart.service.model.report;

import com.fstart.service.enumeration.EReportStatus;
import lombok.*;

/**
 * ProjectReportData
 *
 * @author: VuongVT2
 * @since: 2022/05/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportProjectData {
    private Long reportId;
    private String reason;
    private EReportStatus status;
    private String violation;
    private String createdAt;
    private String updatedAt;
    private String projectId;
    private String logo;
    private String projectTitle;

}
