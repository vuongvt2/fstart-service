package com.fstart.service.model.project;

import lombok.*;

/**
 * ProjectReportData
 *
 * @author: VuongVT2
 * @since: 2022/04/1
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReportData {

    private String id;
    private String logo;
    private String title;
    private String subTitle;
    private String description;
    private String violation;
    private String reason;

}
