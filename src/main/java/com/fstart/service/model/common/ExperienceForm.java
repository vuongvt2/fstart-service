package com.fstart.service.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ExperienceForm
 *
 * @author: VuongVT2
 * @since: 2022/03/29
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceForm {
    private Long id;
    private String title;
    private String company;
    private String address;
    private String description;
    private String startWorking;
    private String endWorking;
    private boolean currentWorkingFlg;
}
