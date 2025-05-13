package com.fstart.service.model.common;

import lombok.*;

/**
 * StatisticalUser
 *
 * @author: VuongVT2
 * @since: 2022/05/23
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticalUser {
    private Long numberOfUsers;
    private Long numberOfActive;
    private Long numberOfNewActive;
    private Long numberOfInactive;
    private Long numberOfBlocked;
}
