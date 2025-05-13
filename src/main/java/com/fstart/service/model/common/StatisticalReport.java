package com.fstart.service.model.common;

import lombok.*;

/**
 * StatisticalReport
 *
 * @author: VuongVT2
 * @since: 2022/05/23
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticalReport {
    private Long numberOfReport;
    private Long numberOfNew;
    private Long numberOfAccepted;
    private Long numberOfReject;

}
