package com.fstart.service.model.common;

import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.entity.Experience;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * ExperienceData
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceData implements Serializable {

    private long id;
    private String title;
    private String company;
    private String address;
    private String description;
    private String startWorking;
    private String endWorking;
    private boolean currentWorkingFlg;

    public static ExperienceData transform(Experience experience) {
        ExperienceData experienceData = DataBuilder.to(experience, ExperienceData.class);

        return experienceData;
    }


}
