package com.fstart.service.model.common;

import lombok.*;

/**
 * StatisticalProject
 *
 * @author: VuongVT2
 * @since: 2022/05/23
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticalProject {
    private Long numberOfProjects;
    private Long numberOfPending;
    private Long numberOfApprove;
    private Long numberOfReject;
    private Long numberOfBlocked;
}
