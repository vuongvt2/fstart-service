package com.fstart.service.model.report;

import com.fstart.service.enumeration.EReportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BaseReportProjectData
 *
 * @author: VuongVT2
 * @since: 2022/05/12
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {
    private Long id;
    private String reason;
    private EReportStatus status;
    private String createdAt;
}
