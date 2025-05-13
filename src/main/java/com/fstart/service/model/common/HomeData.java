package com.fstart.service.model.common;

import com.fstart.service.model.event.BaseEventData;
import com.fstart.service.model.home.HomeNewsData;
import com.fstart.service.model.home.HomeProjectData;
import com.fstart.service.model.home.HomeStartupData;
import com.fstart.service.model.home.HomeUserData;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * HomeData
 *
 * @author VuongVT2
 * @since 2022/04/12
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeData implements Serializable {

    private List<HomeUserData> users;
    private List<HomeProjectData> projects;
    private List<HomeNewsData> news;
    private List<HomeStartupData> startups;
    private List<BaseEventData> events;


}
