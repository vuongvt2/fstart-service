package com.fstart.service.model.discussion;

import lombok.*;

/**
 * EventParticipantDiscussionCreateForm
 *
 * @author: VuongVT2
 * @since: 2022/05/27
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantDiscussionCreateForm {
    private Long eventParticipantId;
    private String content;
}
