package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ExistenceException;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.logging.AppLogger;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.common.utils.IDGenerator;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.component.S3Component;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.EEventParticipantStatus;
import com.fstart.service.enumeration.EPosition;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.discussion.DiscussionData;
import com.fstart.service.model.event.*;
import com.fstart.service.model.project.DocumentData;
import com.fstart.service.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final Message message;
    private final S3Component s3Component;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final PositionRepository positionRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final DocumentRepository documentRepository;


    public EventServiceImpl(final Message message,
                            final S3Component s3Component,
                            final EventRepository eventRepository,
                            final UserRepository userRepository,
                            final ProjectRepository projectRepository,
                            final EventParticipantRepository eventParticipantRepository,
                            final ProjectTeamMemberRepository projectTeamMemberRepository,
                            final PositionRepository positionRepository,
                            final VoteRepository voteRepository,
                            final CommentRepository commentRepository,
                            final DocumentRepository documentRepository) {
        this.message = message;
        this.s3Component = s3Component;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.eventParticipantRepository = eventParticipantRepository;
        this.projectTeamMemberRepository = projectTeamMemberRepository;
        this.positionRepository = positionRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DataWrapper createEvent(final EventFormCreate eventFormCreate, final String userId) {
        //check is Admin
        checkUserIsAdmin(userId);

        String id = "";
        do {
            id = IDGenerator.generateID(eventRepository, 10);
        } while (eventRepository.existsById(id));
        String eventBanner;
        try {
            eventBanner = s3Component.upload("events/" + id + "/banner", eventFormCreate.getBanner());
        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }
        Event event = Event.builder()
                .id(id)
                .banner(eventBanner)
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .description(eventFormCreate.getDescription())
                .endTime(eventFormCreate.getEndTime())
                .startTime(eventFormCreate.getStartTime())
                .title(eventFormCreate.getTitle())
                .build();

        eventRepository.saveAndFlush(event);

        return DataWrapper.builder()
                .data(event.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public DataWrapper updateEvent(final EventFormUpdate eventFormUpdate, final String userId) {
        //check is Admin
        checkUserIsAdmin(userId);

        Event event = eventRepository.findById(eventFormUpdate.getId())
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
//
        if (Objects.nonNull(eventFormUpdate.getBanner())) {
            try {
                String fileName = event.getBanner().substring(event.getBanner().lastIndexOf("/") + 1);
                s3Component.delete("events/" + event.getId() + "/banner", fileName);
                String eventBanner = s3Component.upload("events/" + eventFormUpdate.getId() + "/banner", eventFormUpdate.getBanner());
                event.setBanner(eventBanner);
            } catch (IOException | URISyntaxException e) {
                AppLogger.errorLog(e.getMessage(), e);
                throw new ServerErrorException(message.getErrorUploadFileError());
            }
        }


        event.setUpdatedAt(TimeUtils.comNowDatetime());
        event.setDescription(eventFormUpdate.getDescription());
        event.setEndTime(eventFormUpdate.getEndTime());
        event.setStartTime(eventFormUpdate.getStartTime());
        event.setTitle(eventFormUpdate.getTitle());

        eventRepository.saveAndFlush(event);

        return DataWrapper.builder()
                .data(event)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<BaseEventData> getAllEvent(final Pageable pageable, final ERole role, final String search) {
        String currentTime = TimeUtils.comNowDatetime();
        Page<Event> listEvent;
        if (ERole.ADMIN != role) {
            listEvent = eventRepository.findAllEvents(pageable, currentTime, search);
        } else {
            listEvent = eventRepository.findAllEventsForAdmin(pageable, search);
        }

        List<BaseEventData> listBaseEventData = listEvent.stream()
                .map(event -> {
                    BaseEventData baseEventData = DataBuilder.to(event, BaseEventData.class);
                    baseEventData.setId(event.getId());
                    baseEventData.setBanner(event.getBanner());
                    baseEventData.setTitle(event.getTitle());
                    baseEventData.setStartTime(event.getStartTime());
                    baseEventData.setEndTime(event.getEndTime());
                    return baseEventData;
                }).collect(Collectors.toList());

        return new PagedResponse<>(listBaseEventData, listEvent.getNumber(), listEvent.getSize(), listEvent.getTotalElements(), listEvent.getTotalPages());
    }

    @Override
    public EventData getEventDetail(final String userId, final String eventId) {
        User user = null;
        if (StringUtils.hasLength(userId)) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        EventData eventData = new EventData();
        eventData.setId(eventId);
        eventData.setBanner(event.getBanner());
        eventData.setTitle(event.getTitle());
        eventData.setDescription(event.getDescription());
        eventData.setCreatedAt(event.getCreatedAt());
        eventData.setUpdatedAt(event.getUpdatedAt());
        eventData.setStartTime(event.getStartTime());
        eventData.setEndTime(event.getEndTime());

        List<Project> projects = projectRepository.findProjectsByEventAndStatus(event.getId(), EEventParticipantStatus.ACCEPTED);
        List<EventParticipantData> eventParticipantDataList = new ArrayList<>();
        for (Project project : projects) {
            EventParticipantData eventParticipantData = EventParticipantData.builder()
                    .logo(project.getLogo())
                    .projectId(project.getId())
                    .subTitle(project.getSubTitle())
                    .title(project.getTitle())
                    .id(eventParticipantRepository.findByProjectAndEvent(project, event).getId())
                    .build();
            Long numberOfVote = voteRepository.countAllByEventParticipant(eventParticipantRepository.findByProjectAndEvent(project, event));
            eventParticipantData.setNumberOfVote(numberOfVote);
            eventParticipantDataList.add(eventParticipantData);
            String projectPosition = null;
            if (Objects.nonNull(user)) {
                ProjectTeamMember projectTeamMember = projectTeamMemberRepository.findByProjectAndUser(project, user);
                if (Objects.nonNull(projectTeamMember)) {
                    projectPosition = projectTeamMember.getPosition().getId();
                }
            }
            eventParticipantData.setProjectPosition(projectPosition);
        }

        eventData.setEventParticipants(eventParticipantDataList);
        return eventData;
    }

    @Override
    public boolean voteForEventParticipant(Long eventParticipantId, String userId) {
        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
//        eventParticipant.setUser(user);

        eventParticipantRepository.save(eventParticipant);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper deleteEvent(final String id, final String userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        checkUserIsAdmin(userId);
        eventParticipantRepository.deleteEventParticipantsByEvent(event);
        eventRepository.delete(event);

        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    /* status = ACCEPTED then Accept  Join Event
       status = REJECT THEN Reject join Event */
    @Override
    public DataWrapper responseJoinEventRequest(final EventParticipantForm eventParticipantForm, final String userId) {

        //check is Admin
        checkUserIsAdmin(userId);

        Event event = eventRepository.findById(eventParticipantForm.getEventId()).orElseThrow(
                () -> new ExistenceException(message.getWarnNoData()));
        Project project = projectRepository.findById(eventParticipantForm.getProjectId()).orElseThrow(
                () -> new ExistenceException(message.getWarnNoData()));
        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantForm.getId()).orElseThrow(
                () -> new ExistenceException(message.getWarnNoData()));

        // Project has joined event, the status cannot be updated
        if (eventParticipant.getStatus().equals(EEventParticipantStatus.ACCEPTED)) {
            throw new ExistenceException(message.getDuplicateData());
        }

        eventParticipant.setId(eventParticipantForm.getId());
        eventParticipant.setStatus(EEventParticipantStatus.valueOf(eventParticipantForm.getStatus()));
        eventParticipant.setProject(project);
        eventParticipant.setEvent(event);
        eventParticipant.setStatus(EEventParticipantStatus.valueOf(eventParticipantForm.getStatus()));
        eventParticipant.setUpdatedAt(TimeUtils.comNowDatetime());

        if (eventParticipant.getStatus().equals(EEventParticipantStatus.ACCEPTED)) {
            eventParticipantRepository.saveAndFlush(eventParticipant);
        } else if (eventParticipant.getStatus().equals(EEventParticipantStatus.REJECTED)) {
            eventParticipantRepository.delete(eventParticipant);
        }

        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    // status = NEW
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper requestJoinEvent(final String eventId, final String projectId, final String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ExistenceException(message.getWarnNoData()));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ExistenceException(message.getWarnNoData()));
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ExistenceException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, user, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }


        EventParticipant eventParticipant = EventParticipant.builder()
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .event(event)
                .project(project)
                .status(EEventParticipantStatus.NEW)
                .build();

        eventParticipantRepository.save(eventParticipant);

        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<EventParticipant> getAllJoinEventRequest(final Pageable pageable, final String userId, final String search, final EEventParticipantStatus status) {
        //check is Admin
        checkUserIsAdmin(userId);

        Page<EventParticipant> listEventParticipants = eventParticipantRepository.findAllJoinEventRequest(pageable, search, status);

        return new PagedResponse<>(listEventParticipants.getContent(), listEventParticipants.getNumber(), listEventParticipants.getSize(), listEventParticipants.getTotalElements(), listEventParticipants.getTotalPages());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DataWrapper deleteParticipant(EventParticipantDeleteForm eventParticipantDeleteForm, final String userId) {
        checkUserIsAdmin(userId);

        Event event = eventRepository.getById(eventParticipantDeleteForm.getEventId());
        Project project = projectRepository.getById(eventParticipantDeleteForm.getProjectId());

        eventParticipantRepository.deleteEventParticipantsByEventAndProject(event, project);
        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean voteProject(final String userId, final Long eventParticipantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (voteRepository.existsByEventParticipantAndUser(eventParticipant, user)) {
            throw new ServerErrorException(message.getDuplicateData());
        }

        Project project = eventParticipant.getProject();

        if (projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
            throw new ServerErrorException(message.getErrorBadRequest());
        }

        Vote vote = Vote.builder()
                .eventParticipant(eventParticipant)
                .user(user)
                .build();
        voteRepository.saveAndFlush(vote);
        return true;
    }

    @Override
    public EventParticipantDetailData getEventParticipantDetail(final String userId, final Long id) {
        User user = null;
        if (StringUtils.hasLength(userId)) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        }
        EventParticipant eventParticipant = eventParticipantRepository.findById(id)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Project project = eventParticipant.getProject();
        boolean voted = false;
        String projectPosition = null;
        if (Objects.nonNull(user)) {
            ProjectTeamMember projectTeamMember = projectTeamMemberRepository.findByProjectAndUser(project, user);
            if (Objects.nonNull(projectTeamMember)) {
                projectPosition = projectTeamMember.getPosition().getId();
            }
            voted = voteRepository.existsByEventParticipantAndUser(eventParticipant, user);
        }
        List<Document> documents = (List<Document>) eventParticipant.getDocuments();
        List<DocumentData> documentDataList = DataBuilder.toList(documents, DocumentData.class);
        List<Discussion> discussionList = (List<Discussion>) eventParticipant.getDiscussions();
        List<DiscussionData> discussionDataList = new ArrayList<>();
        boolean owner = false;
        for (Discussion discussion : discussionList) {
            DiscussionData discussionData = DataBuilder.to(discussion, DiscussionData.class);
            discussionData.setId(discussion.getId());
            discussionData.setFullName(discussion.getUser().getLastName() + " " + discussion.getUser().getFirstName());
            discussionData.setContent(discussion.getContent());
            discussionData.setCreatedAt(discussion.getCreatedAt());
            discussionData.setUpdatedAt(discussion.getUpdatedAt());
            discussionData.setNumberOfComment(commentRepository.countAllByDiscussion(discussion));
            discussionData.setAvatar(discussion.getUser().getAvatar());
            discussionData.setUserId(discussion.getUser().getId());
            if (Objects.nonNull(user)) {
                owner = discussion.getUser().equals(user);
            }
            discussionData.setOwner(owner);
            discussionDataList.add(discussionData);
        }
        EventParticipantDetailData eventParticipantDetailData = EventParticipantDetailData.builder()
                .eventParticipantId(eventParticipant.getId())
                .projectLogo(project.getLogo())
                .projectTitle(project.getTitle())
                .description(eventParticipant.getDescription())
                .projectSubTitle(project.getSubTitle())
                .numberOfVote(voteRepository.countAllByEventParticipant(eventParticipant))
                .projectPosition(projectPosition)
                .eventParticipantDocuments(documentDataList)
                .eventParticipantDiscussions(discussionDataList)
                .voted(voted)
                .build();

        return eventParticipantDetailData;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<DocumentData> uploadDocuments(final String userId, final EventParticipantDocumentForm eventParticipantDocumentForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantDocumentForm.getEventParticipantId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUser(eventParticipant.getProject(), user)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        MultipartFile[] documentFiles = eventParticipantDocumentForm.getDocuments();
        List<Document> documents = new ArrayList<>();
        if (Objects.nonNull(documentFiles) && documentFiles.length != 0) {
            try {
                for (var i = 0; i < documentFiles.length; i++) {
                    String link = s3Component.upload("eventParticipants/" + eventParticipantDocumentForm.getEventParticipantId() + "/documents", documentFiles[i]);
                    Document document = Document.builder()
                            .link(link)
                            .name(documentFiles[i].getOriginalFilename())
                            .eventParticipant(eventParticipant)
                            .build();
                    documents.add(document);
                }
            } catch (IOException | URISyntaxException e) {
                AppLogger.errorLog(e.getMessage(), e);
                throw new ServerErrorException(message.getErrorUploadFileError());
            }
            documentRepository.saveAllAndFlush(documents);
        }

        List<DocumentData> documentDataList = DataBuilder.toList(documents, DocumentData.class);
        return documentDataList;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean updateDescription(String userId, EventParticipantUpdateForm eventParticipantUpdateForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantUpdateForm.getEventParticipantId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!EEventParticipantStatus.ACCEPTED.equals(eventParticipant.getStatus()) ||
                !projectTeamMemberRepository.existsByProjectAndUserAndPosition(eventParticipant.getProject(), user, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        eventParticipant.setDescription(eventParticipantUpdateForm.getDescription());
        eventParticipantRepository.saveAndFlush(eventParticipant);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean unvoteProject(final String userId, final Long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        EventParticipant eventParticipant = eventParticipantRepository.findById(id)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Vote vote = voteRepository.findByEventParticipantAndUser(eventParticipant, user);
        if (Objects.isNull(vote)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        voteRepository.delete(vote);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean deleteDocument(final String userId, final Long eventParticipantId, final Long documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUser(eventParticipant.getProject(), user)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        String fileName = document.getLink().substring(document.getLink().lastIndexOf("/") + 1);
        s3Component.delete("eventParticipants/" + eventParticipantId + "/documents", fileName);
        documentRepository.delete(document);
        return true;
    }

    private void checkUserIsAdmin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        if (!user.getRole().getId().equals(ERole.valueOf("ADMIN"))) {
            System.out.println(user.getRole().getId().equals(ERole.valueOf("ADMIN")));
            throw new BadCredentialsException(message.getErrorUnauthorized());
        }
    }
}
