package com.fstart.service.service;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.common.constant.Constant;
import com.fstart.service.common.constant.EmailConstant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.EInvitationStatus;
import com.fstart.service.enumeration.EInvitationType;
import com.fstart.service.enumeration.EPosition;
import com.fstart.service.enumeration.EUserInvitationType;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.email.EmailMessageConfig;
import com.fstart.service.model.invitation.InvitationData;
import com.fstart.service.model.invitation.ProjectInvitationForm;
import com.fstart.service.model.invitation.RequestForm;
import com.fstart.service.model.invitation.UserInvitationData;
import com.fstart.service.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.*;

/**
 * InvitationServiceImpl
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Service
public class InvitationServiceImpl implements InvitationService {

    private final Message message;
    private final Constant constant;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final InvitationRepository invitationRepository;
    private final UserInvitationRepository userInvitationRepository;
    private final PositionRepository positionRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final EmailService emailService;
    private final EmailConstant emailConstant;

    public InvitationServiceImpl(final Message message,
                                 final Constant constant,
                                 final RoleRepository roleRepository,
                                 final UserRepository userRepository,
                                 final ProjectRepository projectRepository,
                                 final ProjectTeamMemberRepository projectTeamMemberRepository,
                                 final InvitationRepository invitationRepository,
                                 final UserInvitationRepository userInvitationRepository,
                                 final PositionRepository positionRepository,
                                 final ProjectPositionRepository projectPositionRepository,
                                 final EmailService emailService,
                                 final EmailConstant emailConstant) {
        this.message = message;
        this.constant = constant;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectTeamMemberRepository = projectTeamMemberRepository;
        this.invitationRepository = invitationRepository;
        this.userInvitationRepository = userInvitationRepository;
        this.positionRepository = positionRepository;
        this.projectPositionRepository = projectPositionRepository;
        this.emailService = emailService;
        this.emailConstant = emailConstant;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper sendInvitation(final String senderId, final ProjectInvitationForm projectInvitationForm) {
        Project project = projectRepository.findById(projectInvitationForm.getProjectId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User projectOwner = userRepository.findById(senderId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, projectOwner, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        Position position = positionRepository.findById(projectInvitationForm.getPositionId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        Invitation invitation = Invitation.builder()
                .status(EInvitationStatus.NEW)
                .type(EInvitationType.INVITATION)
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .project(project)
                .position(position)
                .build();

        invitationRepository.saveAndFlush(invitation);
        UserInvitation sender = UserInvitation.builder()
                .user(userRepository.findById(senderId)
                        .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                .invitation(invitation)
                .type(EUserInvitationType.SENDER)
                .build();

        User receiverUser = userRepository.findById(projectInvitationForm.getReceiverId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (projectTeamMemberRepository.existsByProjectAndUser(project, receiverUser) ||
                projectTeamMemberRepository.countExistByUserAndProjectAndPosition(projectInvitationForm.getReceiverId(), projectInvitationForm.getProjectId(), projectInvitationForm.getPositionId()) > 0) {
            throw new ServerErrorException(message.getDuplicateData());
        }

        UserInvitation receiver = UserInvitation.builder()
                .user(receiverUser)
                .invitation(invitation)
                .type(EUserInvitationType.RECEIVER)
                .build();
        List<UserInvitation> userInvitations = Arrays.asList(sender, receiver);
        userInvitationRepository.saveAllAndFlush(userInvitations);

        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(receiverUser.getEmail());
        config.setSubject(emailConstant.getSUBJECT_INVITATION());
        config.setTemplate("invitation");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", receiverUser.getLastName() + " " + receiverUser.getFirstName());
        templateModel.put("sender", sender.getUser().getLastName() + " " + sender.getUser().getFirstName());
        templateModel.put("projectTitle", project.getTitle());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());

//        String invitationAndRequestPage = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURIEmail(), receiverUser.getId(), "invitationAndRequestPage"))
//                .toString();

        templateModel.put("invitationAndRequestPage", "http://fstart.begin.vn/invitation-request");

//        String url = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURIEmail(), receiverUser.getId(), "support"))
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
    public DataWrapper sendRequest(final String senderId, final RequestForm requestForm) {
        Project project = projectRepository.findById(requestForm.getProjectId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        Position position = positionRepository.findById(requestForm.getPositionId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User userSend = userRepository.getById(senderId);

        Invitation request = Invitation.builder()
                .status(EInvitationStatus.NEW)
                .type(EInvitationType.REQUEST)
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .project(project)
                .position(position)
                .build();
        invitationRepository.saveAndFlush(request);

        UserInvitation sender = UserInvitation.builder()
                .user(userSend)
                .invitation(request)
                .type(EUserInvitationType.SENDER)
                .build();

        ProjectTeamMember projectOwner = projectTeamMemberRepository.findByProjectAndPosition(project, positionRepository.getById(EPosition.PO.name()));

        UserInvitation receiver = UserInvitation.builder()
                .user(projectOwner.getUser())
                .invitation(request)
                .type(EUserInvitationType.RECEIVER)
                .build();
        List<UserInvitation> userInvitations = Arrays.asList(sender, receiver);
        userInvitationRepository.saveAllAndFlush(userInvitations);

        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(projectOwner.getUser().getEmail());
        config.setSubject(emailConstant.getSUBJECT_REQUEST());
        config.setTemplate("request");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", projectOwner.getUser().getLastName() + " " + projectOwner.getUser().getFirstName());
        templateModel.put("sender", sender.getUser().getLastName() + " " + sender.getUser().getFirstName());
        templateModel.put("projectTitle", project.getTitle());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());

//        String invitationAndRequestPage = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURIEmail(), projectOwner.getUser().getId(), "invitationAndRequestPage"))
//                .toString();

        templateModel.put("invitationAndRequestPage", "http://fstart.begin.vn/invitation-request");

//        String url = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURIEmail(), projectOwner.getUser().getId(), "support"))
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
    public DataWrapper acceptInvitation(final Long id, final String userId) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        Project project = invitation.getProject();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!userInvitationRepository.existsByInvitationAndUserAndType(invitation, user, EUserInvitationType.RECEIVER)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        if (projectTeamMemberRepository.existsByProjectAndUser(project, user)) {
            throw new ServerErrorException(message.getDuplicateData());
        }

        boolean existPosition = projectPositionRepository.existsByPositionAndProject(invitation.getPosition(), project);

        ProjectPosition projectPosition = projectPositionRepository.findByPositionAndProject(invitation.getPosition(), project);
        if (!existPosition) {
            throw new ServerErrorException(message.getOutOfSlot());
        }

        invitation.setStatus(EInvitationStatus.ACCEPTED);
        invitation.setUpdatedAt(TimeUtils.comNowDatetime());
        invitationRepository.saveAndFlush(invitation);

        ProjectTeamMember projectTeamMember = ProjectTeamMember.builder()
                .user(user)
                .project(project)
                .position(invitation.getPosition())
                .build();
        projectTeamMemberRepository.saveAndFlush(projectTeamMember);

        projectPosition.setAvailableSlot(projectPosition.getAvailableSlot() - 1);
        if (projectPosition.getAvailableSlot() == 0) {
            projectPositionRepository.delete(projectPosition);
        } else {
            projectPositionRepository.saveAndFlush(projectPosition);
        }

        List<Invitation> newInvitations = invitationRepository.getInvitationByUserAndProject(user.getId(), project.getId());
        newInvitations.forEach(newInvitation -> {
            if (newInvitation.getType().equals(EInvitationType.INVITATION)) {
                newInvitation.setStatus(EInvitationStatus.REFUSED);
            } else {
                newInvitation.setStatus(EInvitationStatus.CANCELED);
            }
        });
        invitationRepository.saveAllAndFlush(newInvitations);


        User projectOwner = projectTeamMemberRepository.findByProjectAndPosition(project, positionRepository.getById(EPosition.PO.name()))
                .getUser();
        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(projectOwner.getEmail());
        config.setSubject(emailConstant.getSUBJECT_ACCEPT_INVITATION());
        config.setTemplate("accept-invitation");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", projectOwner.getLastName() + " " + projectOwner.getFirstName());
        templateModel.put("sender", user.getLastName() + " " + user.getFirstName());
        templateModel.put("projectTitle", project.getTitle());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());


//        String invitationAndRequestPage = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURIEmail(), projectOwner.getId(), "invitationAndRequestPage"))
//                .toString();

        templateModel.put("invitationAndRequestPage", "http://fstart.begin.vn/my-project");

//        String url = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURIEmail(), projectOwner.getId(), "support"))
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
    public DataWrapper acceptRequest(final Long id, final String userId) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        Project project = invitation.getProject();
        User user = userRepository.getById(userId);

        if (!projectTeamMemberRepository.existsByProjectAndUserAndPosition(invitation.getProject(), user, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        boolean existPosition = projectPositionRepository.existsByPositionAndProject(invitation.getPosition(), project);

        ProjectPosition projectPosition = projectPositionRepository.findByPositionAndProject(invitation.getPosition(), project);
        if (!existPosition) {
            throw new ServerErrorException(message.getOutOfSlot());
        }

        UserInvitation userInvitation = userInvitationRepository.findByInvitationAndType(invitation, EUserInvitationType.SENDER)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User sender = userInvitation.getUser();

        invitation.setStatus(EInvitationStatus.ACCEPTED);
        invitation.setUpdatedAt(TimeUtils.comNowDatetime());
        invitationRepository.saveAndFlush(invitation);

        ProjectTeamMember projectTeamMember = ProjectTeamMember.builder()
                .user(sender)
                .project(project)
                .position(invitation.getPosition())
                .build();
        projectTeamMemberRepository.saveAndFlush(projectTeamMember);

        Long availableSlot = projectPosition.getAvailableSlot();
        if (availableSlot == 1) {
            projectPositionRepository.delete(projectPosition);
            List<Invitation> newInvitations = invitationRepository.getInvitationByUserAndProject(user.getId(), project.getId());
            newInvitations.forEach(newInvitation -> {
                if (newInvitation.getType().equals(EInvitationType.REQUEST)) {
                    newInvitation.setStatus(EInvitationStatus.REFUSED);
                } else {
                    newInvitation.setStatus(EInvitationStatus.CANCELED);
                }
            });
            invitationRepository.saveAllAndFlush(newInvitations);
        } else {
            projectPosition.setAvailableSlot(projectPosition.getAvailableSlot() - 1);
            projectPositionRepository.saveAndFlush(projectPosition);
        }

        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(sender.getEmail());
        config.setSubject(emailConstant.getSUBJECT_ACCEPT_REQUEST());
        config.setTemplate("accept-request");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", sender.getLastName() + " " + sender.getFirstName());
        templateModel.put("projectTitle", project.getTitle());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());

//        String myProjectPage = new StringBuilder(constant.getAppHost())
//                .append(String.format(constant.getAppRedirectURIEmail(), sender.getId(), "myProjectPage"))
//                .toString();

        templateModel.put("myProjectPage", "http://fstart.begin.vn/my-project");

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
    public DataWrapper refuseRequest(final Long id, final String userId) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        if (!projectTeamMemberRepository.existsByProjectAndUserAndPosition(invitation.getProject(), user, positionRepository.getById(EPosition.PO.name()))) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        invitation.setStatus(EInvitationStatus.REFUSED);
        invitation.setUpdatedAt(TimeUtils.comNowDatetime());
        invitationRepository.saveAndFlush(invitation);

        return DataWrapper.builder()
                .data(true)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper refuseInvitation(final Long id, final String userId) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!userInvitationRepository.existsByInvitationAndUserAndType(invitation, user, EUserInvitationType.RECEIVER)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        invitation.setUpdatedAt(TimeUtils.comNowDatetime());
        invitation.setStatus(EInvitationStatus.REFUSED);
        invitationRepository.saveAndFlush(invitation);
        return DataWrapper.builder()
                .data(id)
                .status(AppConstant.SUCCESS)
                .build();
    }

    @Override
    public PagedResponse<InvitationData> getAllInvitationsAndRequestsByType(final String userId, final EUserInvitationType type, final Pageable pageable) {
        Page<Invitation> invitations = invitationRepository.findAllByUserIdAndType(userId, type, EInvitationStatus.NEW, pageable);
        List<InvitationData> invitationDataList = new ArrayList<>();
        invitations.forEach(invitation -> {
            InvitationData invitationData = new InvitationData();
            invitationData.setInvitation(invitation);
            List<UserInvitation> userInvitations = userInvitationRepository.findAllByInvitation(invitation);
            UserInvitationData userInvitationData = new UserInvitationData();
            userInvitations.forEach(userInvitation -> {
                if (!userInvitation.getType().equals(type)) {
                    userInvitationData.setType(userInvitation.getType().name());
                    userInvitationData.setFullName(userInvitation.getUser().getLastName() + " " + userInvitation.getUser().getFirstName());
                    userInvitationData.setUserId(userInvitation.getUser().getId());
                }
            });
            invitationData.setUserInvitationData(userInvitationData);
            invitationDataList.add(invitationData);
        });
        return new PagedResponse<>(invitationDataList, invitations.getNumber(), invitations.getSize(), invitations.getTotalElements(), invitations.getTotalPages());

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DataWrapper cancelRequestOrInvitation(final Long id, final String userId) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!userInvitationRepository.existsByInvitationAndUserAndType(invitation, user, EUserInvitationType.SENDER)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        invitation.setUpdatedAt(TimeUtils.comNowDatetime());
        invitation.setStatus(EInvitationStatus.CANCELED);
        invitationRepository.saveAndFlush(invitation);
        return DataWrapper.builder()
                .data(id)
                .status(AppConstant.SUCCESS)
                .build();
    }
}