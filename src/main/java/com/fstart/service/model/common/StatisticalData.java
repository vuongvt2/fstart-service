package com.fstart.service.model.common;

import lombok.*;

/**
 * StatisticalData
 *
 * @author: VuongVT2
 * @since: 2022/05/23
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticalData {
    private StatisticalUser user;
    private StatisticalProject project;
    private StatisticalReport report;
}
