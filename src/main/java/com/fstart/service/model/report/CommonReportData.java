package com.fstart.service.model.report;

import lombok.*;

/**
 * NumberOfReportData
 *
 * @author: VuongVT2
 * @since: 2022/05/12
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonReportData {
    private long numberOfNewReport;
    private long numberOfAcceptedReport;
    private long numberOfRejectedReport;
}
