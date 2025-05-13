package com.fstart.service.model.home;

import com.fstart.service.model.common.CommonData;
import lombok.*;

import java.util.List;

/**
 * HomeUserData
 *
 * @author: VuongVT2
 * @since: 2022/04/13
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeUserData {

    private String id;
    private String firstName;
    private String lastName;
    private String avatar;
    private String major;
    private String status;
    private List<CommonData> positions;
    private List<CommonData> fields;
    private Long numberOfReport;

}
