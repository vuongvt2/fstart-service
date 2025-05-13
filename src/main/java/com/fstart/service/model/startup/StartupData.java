package com.fstart.service.model.startup;

import com.fstart.service.model.common.CommonData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * StartupData
 *
 * @author: VuongVT2
 * @since: 2022/01/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartupData {
    private Long id;
    private String logo;
    private String name;
    private String shortDescription;
    private String description;
    private String originalLink;
    private int founded;
    private int startupSize;
    private String status;
    private List<FounderData> founders;
    private List<CommonData> fields;
    private CommonData country;
}
