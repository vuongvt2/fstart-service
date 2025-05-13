package com.fstart.service.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ViolationReportData
 *
 * @author: VuongVT2
 * @since: 2022/05/16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViolationReportData {
    private String violation;
    private List<ReportData> reportDataList;
}
