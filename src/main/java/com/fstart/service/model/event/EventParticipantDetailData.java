package com.fstart.service.model.event;

import com.fstart.service.model.discussion.DiscussionData;
import com.fstart.service.model.project.DocumentData;
import lombok.*;

import java.util.List;

/**
 * EventParticipantDetailData
 *
 * @author: VuongVT2
 * @since: 2022/05/29
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantDetailData {
    private String projectTitle;
    private String projectSubTitle;
    private String projectLogo;
    private Long numberOfVote;
    private Long eventParticipantId;
    private String description;
    private String projectPosition;
    private boolean voted;
    private List<DocumentData> eventParticipantDocuments;
    private List<DiscussionData> eventParticipantDiscussions;
}
