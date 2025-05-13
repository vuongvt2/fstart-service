package com.fstart.service.model.home;

import lombok.*;

/**
 * HomeStartupData
 *
 * @author: VuongVT2
 * @since: 2022/04/13
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeStartupData {
    private Long id;
    private String logo;
    private String name;
    private String shortDescription;
}
