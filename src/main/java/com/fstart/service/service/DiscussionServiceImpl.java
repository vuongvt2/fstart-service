package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ExistenceException;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.EDiscussionStatus;
import com.fstart.service.enumeration.EProjectPrivacy;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.discussion.*;
import com.fstart.service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DiscussionServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/01/19
 */
@Service
public class DiscussionServiceImpl implements DiscussionService {

    private final Message message;

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final DiscussionRepository discussionRepository;
    private final CommentRepository commentRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;

    public DiscussionServiceImpl(final Message message,
                                 final UserRepository userRepository,
                                 final ProjectRepository projectRepository,
                                 final DiscussionRepository discussionRepository,
                                 final CommentRepository commentRepository,
                                 final ProjectTeamMemberRepository projectTeamMemberRepository,
                                 final EventParticipantRepository eventParticipantRepository,
                                 final EventRepository eventRepository) {
        this.message = message;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.discussionRepository = discussionRepository;
        this.commentRepository = commentRepository;
        this.projectTeamMemberRepository = projectTeamMemberRepository;
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public DataWrapper getDiscussionInProjectByUserId(String projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        List<Discussion> discussions = new ArrayList<>();

        if (EProjectPrivacy.PUBLIC.equals(project.getPrivacy())) {
            discussions = discussionRepository.findAllByProject(project);
        } else {
            if (Objects.nonNull(userId)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
                if (projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
                    discussions = discussionRepository.findAllByProject(project);
                }
            }
        }

        return getListDataDiscussionWrapper(userId, discussions);
    }

    @Override
    public List<DiscussionData> getProjectDiscussionsInEvent(final String userId, final String projectId, final String eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        EventParticipant eventParticipant = eventParticipantRepository.findByProjectAndEvent(project, event);

        List<Discussion> discussions = discussionRepository.findAllDiscussionsByEventParticipant(eventParticipant);
        List<DiscussionData> discussionDataList = discussions.stream()
                .map(discussion -> {
                    DiscussionData discussionData = DataBuilder.to(discussion, DiscussionData.class);
                    discussionData.setId(discussion.getId());
                    discussionData.setFullName(discussion.getUser().getLastName() + " " + discussion.getUser().getFirstName());
                    discussionData.setAvatar(discussion.getUser().getAvatar());
                    discussionData.setContent(discussion.getContent());
                    discussionData.setCreatedAt(discussion.getCreatedAt());
                    discussionData.setUpdatedAt(discussion.getUpdatedAt());
                    discussionData.setNumberOfComment(Long.valueOf(discussion.getComments().size()));
                    discussionData.setOwner(discussion.getUser().equals(user));
                    discussionData.setUserId(discussion.getUser().getId());
                    return discussionData;
                }).collect(Collectors.toList());

        return discussionDataList;
    }

    @Override
    public DataWrapper getAllPublicDiscussion() {
        return null;
    }

    @Override
    public DataWrapper getAllDiscussionsByContent(final String userId, final String content) {
        List<Discussion> discussions = discussionRepository.findAllByContent(content);
        return getListDataDiscussionWrapper(userId, discussions);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper createProjectDiscussion(final ProjectDiscussionCreateForm projectDiscussionCreateForm, final String userId) {
        Project project = projectRepository.findById(projectDiscussionCreateForm.getProjectId())
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!EProjectPrivacy.PUBLIC.equals(project.getPrivacy())) {
            if (!projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
                throw new ServerErrorException(message.getErrorUnauthorized());
            }
        }

        Discussion discussion = Discussion.builder()
                .content(projectDiscussionCreateForm.getContent())
                .status(EDiscussionStatus.NEW)
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .user(user)
                .project(project)
                .build();
        discussionRepository.saveAndFlush(discussion);
        DiscussionData discussionData = getDataDiscussionWrapper(user, discussion);

        return DataWrapper.builder()
                .data(discussionData)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper updateDiscussion(final DiscussionUpdateForm discussionUpdateForm, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Discussion discussion = discussionRepository.findById(discussionUpdateForm.getId())
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!discussion.getUser().equals(user)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        discussion.setContent(discussionUpdateForm.getContent());
        discussion.setUpdatedAt(TimeUtils.comNowDatetime());
        discussionRepository.saveAndFlush(discussion);
        return DataWrapper.builder()
                .data(discussion.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper deleteDiscussion(final Long id, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!discussion.getUser().equals(user)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        commentRepository.deleteAllByDiscussion(discussion);
        discussionRepository.delete(discussion);
        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper createEventParticipantDiscussion(final EventParticipantDiscussionCreateForm eventParticipantDiscussionForm, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantDiscussionForm.getEventParticipantId())
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Discussion discussion = Discussion.builder()
                .content(eventParticipantDiscussionForm.getContent())
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .user(user)
                .status(EDiscussionStatus.NEW)
                .eventParticipant(eventParticipant)
                .build();
        discussionRepository.saveAndFlush(discussion);
        DiscussionData discussionData = getDataDiscussionWrapper(user, discussion);
        return DataWrapper.builder()
                .data(discussionData)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public List<DiscussionData> getDiscussionsByProject(final String projectId, final String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!EProjectPrivacy.PUBLIC.equals(project.getPrivacy())) {
            if (!projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
                throw new ServerErrorException(message.getErrorUnauthorized());
            }
        }
        List<Discussion> discussions = discussionRepository.findAllByProject(project);
        List<DiscussionData> discussionDataList = null;
        if (Objects.nonNull(discussions)) {
            discussionDataList= discussions.stream()
                    .map(discussion -> {
                        DiscussionData discussionData = DataBuilder.to(discussion, DiscussionData.class);
                        discussionData.setFullName(discussion.getUser().getLastName() + " " + discussion.getUser().getFirstName());
                        discussionData.setAvatar(discussion.getUser().getAvatar());
                        discussionData.setNumberOfComment(commentRepository.countAllByDiscussion(discussion));
                        discussionData.setOwner(discussion.getUser().equals(user));
                        discussionData.setUserId(discussion.getUser().getId());
                        return discussionData;
                    }).collect(Collectors.toList());

        }
        return discussionDataList;
    }


    private DataWrapper getListDataDiscussionWrapper(final String userId, final List<Discussion> discussions) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        List<DiscussionData> discussionDataList = new ArrayList<>();
        discussions.forEach(discussion -> {
            DiscussionData discussionData = getDataDiscussionWrapper(user, discussion);
            discussionDataList.add(discussionData);
        });
        return DataWrapper.builder()
                .data(discussionDataList)
                .status(AppConstant.SUCCESS)
                .build();
    }

    private DiscussionData getDataDiscussionWrapper(User user, Discussion discussion) {
        DiscussionData discussionData = DataBuilder.to(discussion, DiscussionData.class);
        String fullName = discussion.getUser().getLastName() + " " + discussion.getUser().getFirstName();
        discussionData.setFullName(fullName);
        discussionData.setAvatar(discussion.getUser().getAvatar());
        discussionData.setNumberOfComment(commentRepository.countAllByDiscussion(discussion));
        discussionData.setOwner(discussion.getUser().equals(user));
        discussionData.setUserId(discussion.getUser().getId());
        return discussionData;
    }
}
