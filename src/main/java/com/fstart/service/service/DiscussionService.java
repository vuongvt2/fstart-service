package com.fstart.service.service;

import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.discussion.*;

import java.util.List;

/**
 * DiscussionService
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
public interface DiscussionService {

    List<DiscussionData> getProjectDiscussionsInEvent(String userId, String projectId, String eventId);

    DataWrapper getDiscussionInProjectByUserId(String projectId, String userId);

    DataWrapper getAllPublicDiscussion();

    DataWrapper getAllDiscussionsByContent(String userId, String content);

    DataWrapper createProjectDiscussion(ProjectDiscussionCreateForm projectDiscussionCreateForm, String userId);

    DataWrapper updateDiscussion(DiscussionUpdateForm discussionUpdateForm, String userId);

    DataWrapper deleteDiscussion(Long id, String userId);

    DataWrapper createEventParticipantDiscussion(EventParticipantDiscussionCreateForm eventParticipantDiscussionForm, String userId);

    List<DiscussionData> getDiscussionsByProject(String projectId, String userId);
}
