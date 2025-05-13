package com.fstart.service.model.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BaseEventData
 *
 * @author: VuongVT2
 * @since: 2022/04/10
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEventData {
    private String id;
    private String title;
    private String banner;
    private String startTime;
    private String endTime;
}