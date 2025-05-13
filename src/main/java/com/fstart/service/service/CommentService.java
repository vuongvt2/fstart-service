package com.fstart.service.service;

import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.discussion.CommentData;
import com.fstart.service.model.discussion.CommentForm;
import com.fstart.service.model.discussion.DiscussionDetailData;
import org.springframework.data.domain.Pageable;

/**
 * CommentService
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
public interface CommentService {
    CommentData createComment(CommentForm commentForm, String userId);

    DataWrapper updateComment(CommentForm commentForm);

    DiscussionDetailData getAllCommentsByDiscussionId(Long discussionId, String userId, Pageable pageable);

    DataWrapper deleteComment(Long id, String userId);
}
