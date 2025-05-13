package com.fstart.service.model.home;

import com.fstart.service.model.common.CommonData;
import lombok.*;

import java.util.List;

/**
 * HomeProjectData
 *
 * @author: VuongVT2
 * @since: 2022/04/13
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeProjectData {
    private String id;
    private String title;
    private String logo;
    private int numberOfMember;
    private int numberOfCurrentMember;
    private List<CommonData> fields;
    private List<CommonData> technologies;
    private String currentPosition;
    private String status;
    private Long numberOfReport;
    private boolean callForInvestment;
}
