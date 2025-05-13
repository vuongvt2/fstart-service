package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Constant;
import com.fstart.service.common.constant.EmailConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ExistenceException;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.logging.AppLogger;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.common.utils.IDGenerator;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.component.S3Component;
import com.fstart.service.dao.ProjectDAO;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.*;
import com.fstart.service.model.common.CommonData;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.discussion.DiscussionData;
import com.fstart.service.model.email.EmailMessageConfig;
import com.fstart.service.model.event.AvailableProjectEventData;
import com.fstart.service.model.home.HomeProjectData;
import com.fstart.service.model.project.*;
import com.fstart.service.repository.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ProjectServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/01/16
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final Message message;
    private final S3Component s3Component;
    private final Constant constant;


    private final ProjectRepository projectRepository;
    private final FieldRepository fieldRepository;
    private final ProjectFieldRepository projectFieldRepository;
    private final TechnologyRepository technologyRepository;
    private final ProjectTechnologyRepository projectTechnologyRepository;
    private final DocumentRepository documentRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final InvitationRepository invitationRepository;
    private final DiscussionRepository discussionRepository;
    private final CommentRepository commentRepository;
    private final UserInvitationRepository userInvitationRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final ProjectReportRepository projectReportRepository;
    private final ReportRepository reportRepository;
    private final VoteRepository voteRepository;
    private final EventRepository eventRepository;

    private final EmailService emailService;
    private final EmailConstant emailConstant;

    private final ProjectDAO projectDAO;

    public ProjectServiceImpl(final Message message,
                              final S3Component s3Component,
                              final Constant constant,
                              final ProjectRepository projectRepository,
                              final FieldRepository fieldRepository,
                              final ProjectFieldRepository projectFieldRepository,
                              final TechnologyRepository technologyRepository,
                              final ProjectTechnologyRepository projectTechnologyRepository,
                              final DocumentRepository documentRepository,
                              final ProjectTeamMemberRepository projectTeamMemberRepository,
                              final UserRepository userRepository,
                              final PositionRepository positionRepository,
                              final ProjectPositionRepository projectPositionRepository,
                              final InvitationRepository invitationRepository,
                              final DiscussionRepository discussionRepository,
                              final CommentRepository commentRepository,
                              final UserInvitationRepository userInvitationRepository,
                              final EventParticipantRepository eventParticipantRepository,
                              final ProjectReportRepository projectReportRepository,
                              final ReportRepository reportRepository,
                              final VoteRepository voteRepository,
                              final EventRepository eventRepository,
                              final EmailService emailService,
                              final EmailConstant emailConstant,
                              final ProjectDAO projectDAO) {
        this.message = message;
        this.s3Component = s3Component;
        this.constant = constant;
        this.projectRepository = projectRepository;
        this.fieldRepository = fieldRepository;
        this.projectFieldRepository = projectFieldRepository;
        this.technologyRepository = technologyRepository;
        this.projectTechnologyRepository = projectTechnologyRepository;
        this.documentRepository = documentRepository;
        this.projectTeamMemberRepository = projectTeamMemberRepository;
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.projectPositionRepository = projectPositionRepository;
        this.invitationRepository = invitationRepository;
        this.discussionRepository = discussionRepository;
        this.commentRepository = commentRepository;
        this.userInvitationRepository = userInvitationRepository;
        this.eventParticipantRepository = eventParticipantRepository;
        this.projectReportRepository = projectReportRepository;
        this.reportRepository = reportRepository;
        this.voteRepository = voteRepository;
        this.eventRepository = eventRepository;
        this.emailService = emailService;
        this.emailConstant = emailConstant;
        this.projectDAO = projectDAO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper createProject(final String userId, final ProjectUpsertForm projectUpsertForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException(message.getErrorUnauthorized()));
        try {
            String id = IDGenerator.generateID(projectRepository, 10);

            Project project = DataBuilder.to(projectUpsertForm, Project.class);
            project.setId(id);
            project.setPrivacy(EProjectPrivacy.valueOf(projectUpsertForm.getPrivacy()));
            project.setStatus(EProjectStatus.PENDING);
            project.setCreatedAt(TimeUtils.comNowDatetime());
            project.setUpdatedAt(TimeUtils.comNowDatetime());

            String logo = s3Component.upload("projects/" + id + "/logo", projectUpsertForm.getLogo());
            project.setLogo(logo);
            projectRepository.save(project);

            ProjectTeamMember projectOwner = ProjectTeamMember.builder()
                    .user(user)
                    .position(positionRepository.findById(EPosition.PO.name())
                            .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                    .project(project)
                    .build();
            projectTeamMemberRepository.saveAndFlush(projectOwner);


            List<String> projectPositions = projectUpsertForm.getProjectPositions();
            List<ProjectPosition> projectPositionList = new ArrayList<>();
            if (Objects.nonNull(projectPositions) && projectPositions.size() != 0) {
                //["id,2,des","id,2,des","id,2,des"]
                projectPositions.forEach(item -> {
                    String positionId = item.split(";")[0];
                    String availableSlot = item.split(";")[1];
                    String description = item.split(";")[2];
                    ProjectPosition projectPosition = ProjectPosition.builder()
                            .project(project)
                            .position(positionRepository.findById(positionId)
                                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                            .availableSlot(Long.parseLong(availableSlot))
                            .description(description)
                            .build();
                    projectPositionList.add(projectPosition);
                });
                projectPositionRepository.saveAllAndFlush(projectPositionList);
            }

            List<ProjectField> projectFields = projectUpsertForm.getFields().stream()
                    .map(fieldId -> ProjectField.builder()
                            .project(project)
                            .field(fieldRepository.findById(fieldId)
                                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                            .build())
                    .collect(Collectors.toList());
            projectFieldRepository.saveAllAndFlush(projectFields);

            List<ProjectTechnology> projectTechnologies = projectUpsertForm.getTechnologies().stream()
                    .map(techId -> ProjectTechnology.builder()
                            .project(project)
                            .technology(technologyRepository.findById(techId)
                                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                            .build())
                    .collect(Collectors.toList());
            projectTechnologyRepository.saveAllAndFlush(projectTechnologies);

            MultipartFile[] documentFiles = projectUpsertForm.getDocuments();
            if (Objects.nonNull(documentFiles) && documentFiles.length != 0) {
                List<Document> documents = new ArrayList<>();
                for (var i = 0; i < documentFiles.length; i++) {
                    String link = s3Component.upload("projects/" + id + "/documents", documentFiles[i]);
                    Document document = Document.builder()
                            .link(link)
                            .name(documentFiles[i].getOriginalFilename())
                            .project(project)
                            .build();
                    documents.add(document);
                }
                documentRepository.saveAllAndFlush(documents);
            }
        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }

        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public ProjectData getProjectById(final String id, final String userId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));


        ProjectData projectData = getProjectDataFrom(project);


        if (!StringUtils.hasLength(userId)) {
            if (!project.getStatus().equals(EProjectStatus.APPROVED)) {
                throw new ServerErrorException(message.getErrorUnauthorized());
            }
            return projectData;
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ServerErrorException(message.getErrorUnauthorized()));
        if (!project.getStatus().equals(EProjectStatus.APPROVED)) {
            if (!user.getRole().getId().equals(ERole.ADMIN)
                    && !projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, user, positionRepository.getById(EPosition.PO.name()))
            ) {
                throw new ServerErrorException(message.getErrorUnauthorized());
            }
        }

        if (projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
            ProjectTeamMember projectTeamMember = projectTeamMemberRepository.findByProjectAndUser(project, user);
            projectData.setPositionInProject(projectTeamMember.getPosition().getId());
        } else {
            projectData.setPositionInProject(null);
        }
        List<Document> documents = documentRepository.findAllByProject(project);
        List<DocumentData> documentDataList = DataBuilder.toList(documents, DocumentData.class);
        projectData.setDocuments(documentDataList);

        List<ProjectPositionData> projectPositionList = getAllRecruitment(userId, project.getId());
        projectData.setProjectPositionList(projectPositionList);
        List<ProjectTeamMemberData> projectTeamMemberList = getAllTeamMember(userId, project.getId());
        projectData.setProjectTeamMemberList(projectTeamMemberList);

        List<Discussion> discussions = discussionRepository.findAllByProject(project);

        List<DiscussionData> discussionDataList = new ArrayList<>();
        boolean owner = false;
        for (Discussion discussion : discussions) {
            DiscussionData discussionData = DataBuilder.to(discussion, DiscussionData.class);
            discussionData.setId(discussion.getId());
            discussionData.setFullName(discussion.getUser().getLastName() + " " + discussion.getUser().getFirstName());
            discussionData.setContent(discussion.getContent());
            discussionData.setCreatedAt(discussion.getCreatedAt());
            discussionData.setUpdatedAt(discussion.getUpdatedAt());
            discussionData.setNumberOfComment(commentRepository.countAllByDiscussion(discussion));
            discussionData.setAvatar(discussion.getUser().getAvatar());
            if (Objects.nonNull(user)) {
                owner = discussion.getUser().equals(user);
            }
            discussionData.setOwner(owner);
            discussionDataList.add(discussionData);
        }
        projectData.setDiscussions(discussionDataList);
        return projectData;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper acceptProject(final String roleId, String projectId) {
        if (!ERole.ADMIN.name().equals(roleId)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        project.setStatus(EProjectStatus.APPROVED);
        projectRepository.saveAndFlush(project);
        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper rejectProject(final String roleId, final String projectId, final String reason) {
        if (!ERole.ADMIN.name().equals(roleId)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        project.setStatus(EProjectStatus.REJECTED);
        project.setReason(reason);
        projectRepository.saveAndFlush(project);
        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<HomeProjectData> filterProject(final ERole role, final String search, final String status,
                                                        final String fieldId, final String technologyId, final int page, final int size) {

        List<Field> fields = StringUtils.hasLength(fieldId) ? fieldRepository.findAllById(Arrays.asList(fieldId.split(","))) : null;
        List<Technology> technologies = StringUtils.hasLength(technologyId) ? technologyRepository.findAllById(Arrays.asList(technologyId.split(","))) : null;

        long totalElements;
        List<Project> projects;

        if (ERole.ADMIN != role) {
            projects = projectDAO.findBySearchAndTechnologyAndFieldAndStatus(search, technologies, fields, AppConstant.DEFAULT_PROJECT_STATUS, size, (page - 1) * size);
            totalElements = projectDAO.countBySearchAndTechnologyAndField(search, technologies, fields, AppConstant.DEFAULT_PROJECT_STATUS);
        } else {
            projects = projectDAO.findBySearchAndTechnologyAndFieldAndStatus(search, technologies, fields, status, size, (page - 1) * size);
            totalElements = projectDAO.countBySearchAndTechnologyAndField(search, technologies, fields, status);
        }
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        List<HomeProjectData> projectDataList = projects.stream()
                .map(project -> {
                    int numberOfCurrentMember = projectTeamMemberRepository.countAllByProject(project);
                    List<Long> availableSlots = projectPositionRepository.getListAvailableSlotProject(project);
                    int numberOfMember = 0;
                    for (Long availableSlot : availableSlots) {
                        numberOfMember += availableSlot;
                    }
                    HomeProjectData homeProjectData = HomeProjectData.builder()
                            .id(project.getId())
                            .title(project.getTitle())
                            .logo(project.getLogo())
                            .numberOfMember(numberOfMember + numberOfCurrentMember)
                            .numberOfCurrentMember(numberOfCurrentMember)
                            .numberOfReport(projectReportRepository.countAllByProject(project))
                            .status(project.getStatus().name())
                            .callForInvestment(project.isCallForInvestment())
                            .build();

                    List<CommonData> fieldDataList = project.getProjectFields().stream()
                            .map(field -> DataBuilder.to(field.getField(), CommonData.class))
                            .collect(Collectors.toList());
                    List<CommonData> technologyDataList = project.getProjectTechnologies().stream()
                            .map(technology -> DataBuilder.to(technology.getTechnology(), CommonData.class))
                            .collect(Collectors.toList());
                    homeProjectData.setFields(fieldDataList);
                    homeProjectData.setTechnologies(technologyDataList);
                    return homeProjectData;
                }).collect(Collectors.toList());


        return new PagedResponse<>(projectDataList, page, size, totalElements, totalPages);
    }

    @Override
    public List<ProjectTeamMemberData> getAllTeamMember(final String userId, final String projectId) {
        // unauthorized user
        if (!StringUtils.hasLength(userId))
            return Collections.emptyList();

        // authorized user
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        List<ProjectTeamMember> projectTeamMembers = new ArrayList<>();
        if (EProjectPrivacy.PUBLIC.equals(project.getPrivacy())) {
            projectTeamMembers = projectTeamMemberRepository.findByProjectAndUserIsNotNull(project);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
            if (projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
                projectTeamMembers = projectTeamMemberRepository.findByProjectAndUserIsNotNull(project);
            }
        }

        return projectTeamMembers
                .stream()
                .map(projectTeamMember -> ProjectTeamMemberData.transform(projectTeamMember))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectPositionData> getAllRecruitment(final String userId, final String projectId) {
        User user = userRepository.getById(userId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        ProjectTeamMember projectTeamMember = projectTeamMemberRepository.findByProjectAndUser(project, user);

        List<ProjectPosition> projectPositionList = projectPositionRepository.findAllByProject(project);
        List<ProjectPositionData> projectPositionDataList = projectPositionList
                .stream()
                .map(projectPosition -> {
                    ProjectPositionData projectPositionData = DataBuilder.to(projectPosition, ProjectPositionData.class);
                    CommonData positionData = DataBuilder.to(projectPosition.getPosition(), CommonData.class);
                    projectPositionData.setRequested(false);
                    if (Objects.nonNull(projectTeamMember)) {
                        projectPositionData.setRequested(true);
                    }
                    Invitation invitation = invitationRepository.getInvitationBy(projectId,
                            userId,
                            Arrays.asList(EInvitationStatus.NEW, EInvitationStatus.ACCEPTED),
                            EUserInvitationType.SENDER, positionData.getId());
                    if (Objects.nonNull(invitation)) {
                        projectPositionData.setRequested(true);
                    }
                    projectPositionData.setPosition(positionData);
                    return projectPositionData;
                }).collect(Collectors.toList());
        return projectPositionDataList;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper blockProject(final String roleId, final String projectId, final String reason) {
        if (!ERole.ADMIN.name().equals(roleId)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        project.setStatus(EProjectStatus.BLOCKED);
        project.setReason(reason);
        projectRepository.saveAndFlush(project);

        EmailMessageConfig config = new EmailMessageConfig();

        ProjectTeamMember projectTeamMember = projectTeamMemberRepository.findByProjectAndPosition(project, positionRepository.getById(EPosition.PO.name()));
        User projectOwner = projectTeamMember.getUser();
        config.setTo(projectOwner.getEmail());
        config.setSubject(emailConstant.getSUBJECT_BLOCK_PROJECT());
        config.setTemplate("block-project");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", projectOwner.getLastName() + " " + projectOwner.getFirstName());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());
        templateModel.put("reason", reason);
        templateModel.put("title", project.getTitle());

//        String url = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURISupport()))
//                .toString();
        templateModel.put("contactSupportLink", "http://fstart.begin.vn/contact");
        config.setTemplateModel(templateModel);

        try {
            emailService.sendEmail(Collections.singletonList(config));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper unblockProject(final String roleId, final String projectId) {
        if (!ERole.ADMIN.name().equals(roleId)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        project.setStatus(EProjectStatus.APPROVED);
        project.setReason("");
        projectRepository.saveAndFlush(project);
        ProjectTeamMember projectTeamMember = projectTeamMemberRepository.findByProjectAndPosition(project, positionRepository.getById(EPosition.PO.name()));
        User projectOwner = projectTeamMember.getUser();
        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(projectOwner.getEmail());
        config.setSubject(emailConstant.getSUBJECT_UNBLOCK_PROJECT());
        config.setTemplate("unblock-project");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", projectOwner.getLastName() + " " + projectOwner.getFirstName());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());
        templateModel.put("title", project.getTitle());

//        String url = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURISupport()))
//                .toString();
        templateModel.put("contactSupportLink", "http://fstart.begin.vn/contact");
        config.setTemplateModel(templateModel);

        try {
            emailService.sendEmail(Collections.singletonList(config));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String uploadLogo(final String userId, final ProjectLogoForm projectLogoForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException(message.getErrorUnauthorized()));

        Project project = projectRepository.findById(projectLogoForm.getProjectId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, user, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        String logo;
        try {
            String fileName = project.getLogo().substring(project.getLogo().lastIndexOf("/") + 1);
            s3Component.delete("projects/" + project.getId() + "/logo", fileName);
            logo = s3Component.upload("projects/" + project.getId() + "/logo", projectLogoForm.getLogo());
            project.setLogo(logo);
            projectRepository.saveAndFlush(project);
        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }
        return logo;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<DocumentData> uploadDocuments(final String userId, final ProjectDocumentForm projectDocumentForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException(message.getErrorUnauthorized()));

        Project project = projectRepository.findById(projectDocumentForm.getProjectId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        MultipartFile[] documentFiles = projectDocumentForm.getDocuments();
        List<Document> documents = new ArrayList<>();
        if (Objects.nonNull(documentFiles) && documentFiles.length != 0) {
            try {
                for (var i = 0; i < documentFiles.length; i++) {
                    String link = s3Component.upload("projects/" + projectDocumentForm.getProjectId() + "/documents", documentFiles[i]);
                    Document document = Document.builder()
                            .link(link)
                            .name(documentFiles[i].getOriginalFilename())
                            .project(project)
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

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean deleteDocument(final String userId, final String projectId, final Long documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException(message.getErrorUnauthorized()));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        String fileName = document.getLink().substring(document.getLink().lastIndexOf("/") + 1);
        s3Component.delete("projects/" + projectId + "/documents", fileName);
        documentRepository.delete(document);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean removeMember(final String userId, final String projectId, final String memberId) {
        User projectOwner = userRepository.getById(userId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, projectOwner, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (projectTeamMemberRepository.existsByProjectAndUserAndPositionNot(project, member, positionRepository.getById(EPosition.PO.name()))) {
            projectTeamMemberRepository.deleteByUserAndProject(member, project);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean leaveProject(final String userId, final String projectId) {
        User user = userRepository.getById(userId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, user, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        } else {
            projectTeamMemberRepository.deleteByUserAndProject(user, project);
            List<Invitation> invitations = invitationRepository.getListInvitationByUserAndProject(userId, projectId);
            userInvitationRepository.deleteAllByInvitationIn(invitations);
            invitationRepository.deleteAll(invitations);
        }

        return true;
    }

    @Override
    public List<AvailableProjectPositionData> getAllProjectPositionByUser(final String userId, final String currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ServerErrorException(message.getErrorUnauthorized()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        List<ProjectTeamMember> projectTeamMembers = projectTeamMemberRepository.findAllByUserAndPosition(currentUser, positionRepository.getById(EPosition.PO.name()));

        if (CollectionUtils.isEmpty(projectTeamMembers)) {
            return null;
        }
        List<Project> projects = projectTeamMembers.stream()
                .filter(projectTeamMember -> EProjectStatus.APPROVED.equals(projectTeamMember.getProject().getStatus()))
                .filter(Objects::nonNull)
                .map(ProjectTeamMember::getProject)
                .collect(Collectors.toList());

        List<AvailableProjectPositionData> availableProjectPositionDataList = projects.stream()
                .map(project -> {
                    boolean inProject = projectTeamMemberRepository.existsByProjectAndUser(project, user);
                    List<ProjectPosition> projectPositionList = projectPositionRepository.findAllByProject(project);
                    List<AvailableProjectPosition> projectPositionDataList = null;
                    if (!CollectionUtils.isEmpty(projectPositionList) && inProject == false) {
                        projectPositionDataList = projectPositionList
                                .stream()
                                .map(projectPosition -> {
                                    AvailableProjectPosition availableProjectPosition = DataBuilder.to(projectPosition, AvailableProjectPosition.class);
                                    CommonData positionData = DataBuilder.to(projectPosition.getPosition(), CommonData.class);
                                    availableProjectPosition.setPosition(positionData);
                                    Long invited = invitationRepository.countByPositionAndProjectAndUserAndStatus(projectPosition.getPosition(), project, user, EInvitationStatus.NEW);
                                    if (invited > 0) {
                                        availableProjectPosition.setInvited(true);
                                    } else {
                                        availableProjectPosition.setInvited(false);
                                    }
                                    return availableProjectPosition;
                                }).collect(Collectors.toList());
                    }


                    AvailableProjectPositionData availableProjectPositionData = AvailableProjectPositionData.builder()
                            .projectId(project.getId())
                            .projectTitle(project.getTitle())
                            .inProject(inProject)
                            .projectPositions(projectPositionDataList)
                            .build();
                    return availableProjectPositionData;
                }).collect(Collectors.toList());

        return availableProjectPositionDataList;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean deleteProject(final String userId, final String projectId) {
        User user = userRepository.getById(userId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, user, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        projectTeamMemberRepository.deleteAllByProject(project);
        projectPositionRepository.deleteAllByProject(project);
        projectFieldRepository.deleteAllByProject(project);
        projectTechnologyRepository.deleteAllByProject(project);

        List<ProjectReport> projectReports = projectReportRepository.findAllByProject(project);
        projectReportRepository.deleteAllByProject(project);

        if (!CollectionUtils.isEmpty(projectReports)) {
            projectReports.forEach(projectReport -> {
                reportRepository.delete(projectReport.getReport());
            });
        }

        documentRepository.deleteAllByProject(project);
        List<Invitation> invitations = invitationRepository.findAllByProject(project);
        userInvitationRepository.deleteAllByInvitationIn(invitations);
        invitationRepository.deleteAll(invitations);

        List<Discussion> discussionsProject = discussionRepository.findAllByProject(project);
        commentRepository.deleteAllByDiscussionIn(discussionsProject);
        discussionRepository.deleteAll(discussionsProject);

        List<EventParticipant> eventParticipants = eventParticipantRepository.findAllByProject(project);


        if (!CollectionUtils.isEmpty(eventParticipants)) {
            eventParticipants.forEach(eventParticipant -> {
                List<Discussion> discussionsEventParticipant = discussionRepository.findAllDiscussionsByEventParticipant(eventParticipant);
                commentRepository.deleteAllByDiscussionIn(discussionsEventParticipant);
                discussionRepository.deleteAll(discussionsEventParticipant);
                voteRepository.deleteAllByEventParticipant(eventParticipant);
                documentRepository.deleteAllByEventParticipant(eventParticipant);
            });
        }
        eventParticipantRepository.deleteAllByProject(project);

        projectRepository.delete(project);
        return true;
    }

    @Override
    public List<AvailableProjectEventData> getAllAvailableProjectEvent(final String eventId, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        List<ProjectTeamMember> projectTeamMembers = projectTeamMemberRepository.findAllByUserAndPosition(user, positionRepository.getById(EPosition.PO.name()));
        if (CollectionUtils.isEmpty(projectTeamMembers)) {
            return null;
        }
        List<AvailableProjectEventData> availableProjectEventDataList = projectTeamMembers.stream()
                .map(projectTeamMember -> {
                    Project project = projectTeamMember.getProject();
                    AvailableProjectEventData availableProjectEventData = AvailableProjectEventData.builder()
                            .projectId(project.getId())
                            .projectTitle(project.getTitle())
                            .inEvent(eventParticipantRepository.existsByProjectAndAndEvent(project, event))
                            .build();
                    return availableProjectEventData;
                }).collect(Collectors.toList());


        return availableProjectEventDataList;
    }

    @Override
    public List<HomeProjectData> getAllProjectByUser(final String userId) {
        List<ProjectTeamMember> projectTeamMembers = projectTeamMemberRepository.findAllByUser(userRepository.getById(userId));
        List<Project> projects = projectTeamMembers.stream()
                .map(ProjectTeamMember::getProject)
                .collect(Collectors.toList());
        return projects
                .stream()
                .map(project -> {
                    int numberOfCurrentMember = projectTeamMemberRepository.countAllByProject(project);
                    List<Long> availableSlots = projectPositionRepository.getListAvailableSlotProject(project);
                    int numberOfMember = 0;
                    for (Long availableSlot : availableSlots) {
                        numberOfMember += availableSlot;
                    }
                    HomeProjectData homeProjectData = HomeProjectData.builder()
                            .id(project.getId())
                            .title(project.getTitle())
                            .logo(project.getLogo())
                            .status(project.getStatus().name())
                            .numberOfMember(numberOfMember + numberOfCurrentMember)
                            .numberOfCurrentMember(numberOfCurrentMember)
                            .callForInvestment(project.isCallForInvestment())
                            .build();
                    ProjectTeamMember projectTeamMember = projectTeamMemberRepository.findByProjectAndUser(project, userRepository.getById(userId));
                    homeProjectData.setCurrentPosition(projectTeamMember.getPosition().getName());
                    List<CommonData> fieldDataList = project.getProjectFields().stream()
                            .map(field -> DataBuilder.to(field.getField(), CommonData.class))
                            .collect(Collectors.toList());
                    List<CommonData> technologyDataList = project.getProjectTechnologies().stream()
                            .map(technology -> DataBuilder.to(technology.getTechnology(), CommonData.class))
                            .collect(Collectors.toList());
                    homeProjectData.setFields(fieldDataList);
                    homeProjectData.setTechnologies(technologyDataList);
                    return homeProjectData;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper updateProject(final String userId, final ProjectFormUpdate projectFormUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException(message.getErrorUnauthorized()));

        Project project = projectRepository.findById(projectFormUpdate.getId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, user, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        project.setTitle(projectFormUpdate.getTitle());
        project.setSubTitle(projectFormUpdate.getSubTitle());
        project.setDescription(projectFormUpdate.getDescription());
        project.setPrivacy(EProjectPrivacy.valueOf(projectFormUpdate.getPrivacy()));
        project.setCallForInvestment(projectFormUpdate.isCallForInvestment());
        project.setUpdatedAt(TimeUtils.comNowDatetime());

        projectRepository.save(project);

        projectFieldRepository.deleteAllByProject(project);
        if (!CollectionUtils.isEmpty(projectFormUpdate.getFields())) {
            List<ProjectField> projectFields = projectFormUpdate.getFields().stream()
                    .map(fieldId -> ProjectField.builder()
                            .project(project)
                            .field(fieldRepository.findById(fieldId)
                                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                            .build())
                    .collect(Collectors.toList());
            projectFieldRepository.saveAllAndFlush(projectFields);
        }

        projectTechnologyRepository.deleteAllByProject(project);
        if (!CollectionUtils.isEmpty(projectFormUpdate.getTechnologies())) {
            List<ProjectTechnology> projectTechnologies = projectFormUpdate.getTechnologies().stream()
                    .map(techId -> ProjectTechnology.builder()
                            .project(project)
                            .technology(technologyRepository.findById(techId)
                                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                            .build())
                    .collect(Collectors.toList());
            projectTechnologyRepository.saveAllAndFlush(projectTechnologies);
        }

        if (!CollectionUtils.isEmpty(projectFormUpdate.getProjectPositions())) {
            projectFormUpdate.getProjectPositions()
                    .forEach(projectPositionForm -> {
                        if (projectPositionForm.isDelete()) {
                            projectPositionRepository.deleteById(projectPositionForm.getId());
                        } else {
                            ProjectPosition projectPosition = ProjectPosition.builder()
                                    .id(projectPositionForm.getId())
                                    .description(projectPositionForm.getDescription())
                                    .availableSlot(projectPositionForm.getAvailableSlot())
                                    .position(positionRepository.getById(projectPositionForm.getPositionId()))
                                    .project(project)
                                    .build();
                            projectPositionRepository.saveAndFlush(projectPosition);
                        }
                    });
        }

        projectTeamMemberRepository.deleteAllByProjectAndPositionNot(project, positionRepository.getById(EPosition.PO.name()));
        if (!CollectionUtils.isEmpty(projectFormUpdate.getProjectTeamMembers())) {
            List<ProjectTeamMember> projectTeamMembers = projectFormUpdate.getProjectTeamMembers()
                    .stream()
                    .map(projectTeamMemberForm -> {
                        ProjectTeamMember projectTeamMember = ProjectTeamMember.builder()
                                .user(userRepository.getById(projectTeamMemberForm.getUserId()))
                                .position(positionRepository.getById(projectTeamMemberForm.getPositionId()))
                                .project(project)
                                .build();
                        return projectTeamMember;
                    }).collect(Collectors.toList());
            projectTeamMemberRepository.saveAllAndFlush(projectTeamMembers);
        }

        return DataWrapper.builder()
                .data(projectFormUpdate.getId())
                .status(AppConstant.SUCCESS)
                .build();
    }

    /**
     * PRIVATE FUNCTION
     */
    private ProjectData getProjectDataFrom(final Project project) {
        List<ProjectField> projectFields = projectFieldRepository.findByProject(project);
        List<ProjectTechnology> projectTechnologies = projectTechnologyRepository.findByProject(project);
        int numberOfCurrentMember = projectTeamMemberRepository.countAllByProject(project);
        List<Long> availableSlots = projectPositionRepository.getListAvailableSlotProject(project);
        int numberOfMember = 0;
        for (Long availableSlot : availableSlots) {
            numberOfMember += availableSlot;
        }
        return ProjectData.transform(project, projectFields, projectTechnologies, numberOfMember + numberOfCurrentMember, numberOfCurrentMember);
    }
}
