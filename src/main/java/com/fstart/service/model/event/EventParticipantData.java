package com.fstart.service.model.event;

import lombok.*;

/**
 * EventParticipantData
 *
 * @author VuongVT2
 * @since 2022/04/01
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantData {

    private Long id;
    private String projectId;
    private String logo;
    private String title;
    private String subTitle;
    private Long numberOfVote;
    private String projectPosition;

}