package com.fstart.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * EventData
 *
 * @author: VuongVT2
 * @since: 2022/04/10
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventData {
    private String id;
    private String title;
    private String banner;
    private String startTime;
    private String description;
    private String endTime;
    private String createdAt;
    private String updatedAt;
    private List<EventParticipantData> eventParticipants;
}