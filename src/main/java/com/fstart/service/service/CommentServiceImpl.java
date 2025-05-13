package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ExistenceException;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.entity.Comment;
import com.fstart.service.entity.Discussion;
import com.fstart.service.entity.User;
import com.fstart.service.enumeration.EDiscussionStatus;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.discussion.CommentData;
import com.fstart.service.model.discussion.CommentForm;
import com.fstart.service.model.discussion.DiscussionData;
import com.fstart.service.model.discussion.DiscussionDetailData;
import com.fstart.service.repository.CommentRepository;
import com.fstart.service.repository.DiscussionRepository;
import com.fstart.service.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * CommentServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final Message message;

    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(final Message message,
                              final DiscussionRepository discussionRepository,
                              final UserRepository userRepository,
                              final CommentRepository commentRepository) {
        this.message = message;
        this.discussionRepository = discussionRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public CommentData createComment(final CommentForm commentForm, String userId) {
        Discussion discussion = discussionRepository.findById(commentForm.getDiscussionId())
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        discussion.setStatus(EDiscussionStatus.COMMENTED);
        Comment comment = Comment.builder()
                .content(commentForm.getContent())
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .discussion(discussion)
                .user(user)
                .build();
        commentRepository.saveAndFlush(comment);
        CommentData commentData = DataBuilder.to(comment, CommentData.class);
        String fullName = comment.getUser().getLastName() + " " + comment.getUser().getFirstName();
        commentData.setUserComment(fullName);
        commentData.setAvatarUserComment(comment.getUser().getAvatar());
        commentData.setUserId(comment.getUser().getId());
        return commentData;
    }

    @Override
    public DataWrapper updateComment(final CommentForm commentForm) {
        Comment comment = commentRepository.findById(commentForm.getId())
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        comment.setContent(commentForm.getContent());
        comment.setUpdatedAt(TimeUtils.comNowDatetime());
        commentRepository.saveAndFlush(comment);
        return DataWrapper.builder()
                .data(comment.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public DiscussionDetailData getAllCommentsByDiscussionId(final Long discussionId, final String userId, Pageable pageable) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        User user = userRepository.getById(userId);
        DiscussionData discussionData = DataBuilder.to(discussion, DiscussionData.class);
        discussionData.setAvatar(discussion.getUser().getAvatar());
        discussionData.setFullName(discussion.getUser().getLastName() + " " + discussion.getUser().getFirstName());
        discussionData.setOwner(discussion.getUser().equals(user));
        discussionData.setNumberOfComment(commentRepository.countAllByDiscussion(discussion));

        Page<Comment> commentList = commentRepository.findAllByDiscussion(discussion, pageable);
        List<CommentData> commentDataList = new ArrayList<>();
        commentList.forEach(comment -> {
            CommentData commentData = DataBuilder.to(comment, CommentData.class);
            String fullName = comment.getUser().getLastName() + " " + comment.getUser().getFirstName();
            commentData.setUserComment(fullName);
            commentData.setAvatarUserComment(comment.getUser().getAvatar());
            commentData.setCommenter((commentRepository.getById(comment.getId())).getUser().getId().equals(userId));
            commentData.setUserId(comment.getUser().getId());
            commentDataList.add(commentData);
        });

        DiscussionDetailData discussionDetailData = DiscussionDetailData.builder()
                .commentDataList(new PagedResponse<>(commentDataList, commentList.getNumber(), commentList.getSize(), commentList.getTotalElements(), commentList.getTotalPages()))
                .discussionData(discussionData)
                .build();

        return discussionDetailData;
    }

    @Override
    public DataWrapper deleteComment(final Long id, final String userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!userId.equals(comment.getUser().getId())
                && !userId.equals(comment.getDiscussion().getUser().getId())) {
            throw new ServerErrorException();
        }
        commentRepository.deleteById(id);
        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }
}
