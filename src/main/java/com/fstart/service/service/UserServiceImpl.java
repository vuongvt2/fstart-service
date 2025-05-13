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
import com.fstart.service.dao.UserDAO;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.enumeration.EUserReportType;
import com.fstart.service.enumeration.EUserStatus;
import com.fstart.service.enumeration.EUserVerificationStatus;
import com.fstart.service.model.common.*;
import com.fstart.service.model.email.EmailMessageConfig;
import com.fstart.service.model.home.HomeUserData;
import com.fstart.service.model.user.UserForm;
import com.fstart.service.model.user.UserOverviewData;
import com.fstart.service.model.user.UserRegisterForm;
import com.fstart.service.repository.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UserServiceImpl
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Service
public class UserServiceImpl implements UserService {

    private final Constant constant;
    private final Message message;
    private final S3Component s3Component;

    private final PasswordEncoder encoder;

    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final UserRepository userRepository;
    private final UserFieldRepository userFieldRepository;
    private final UserSkillRepository userSkillRepository;
    private final ExperienceRepository experienceRepository;
    private final RoleRepository roleRepository;
    private final MajorRepository majorRepository;
    private final FieldRepository fieldRepository;
    private final TechnologyRepository technologyRepository;
    private final PositionRepository positionRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final EmailService emailService;
    private final EmailConstant emailConstant;
    private final UserReportRepository userReportRepository;

    private final UserDAO userDAO;

    public UserServiceImpl(final Constant constant,
                           final Message message,
                           final S3Component s3Component,
                           final PasswordEncoder encoder,
                           final ProjectTeamMemberRepository projectTeamMemberRepository,
                           final UserRepository userRepository,
                           final UserFieldRepository userFieldRepository,
                           final UserSkillRepository userSkillRepository,
                           final ExperienceRepository experienceRepository,
                           final MajorRepository majorRepository,
                           final RoleRepository roleRepository,
                           final FieldRepository fieldRepository,
                           final TechnologyRepository technologyRepository,
                           final PositionRepository positionRepository,
                           final UserPositionRepository userPositionRepository,
                           final UserVerificationRepository userVerificationRepository,
                           final EmailService emailService,
                           final EmailConstant emailConstant,
                           final UserReportRepository userReportRepository,
                           final UserDAO userDAO) {
        this.constant = constant;
        this.message = message;
        this.s3Component = s3Component;
        this.encoder = encoder;
        this.projectTeamMemberRepository = projectTeamMemberRepository;
        this.userRepository = userRepository;
        this.userFieldRepository = userFieldRepository;
        this.userSkillRepository = userSkillRepository;
        this.experienceRepository = experienceRepository;
        this.technologyRepository = technologyRepository;
        this.majorRepository = majorRepository;
        this.roleRepository = roleRepository;
        this.fieldRepository = fieldRepository;
        this.positionRepository = positionRepository;
        this.userPositionRepository = userPositionRepository;
        this.userVerificationRepository = userVerificationRepository;
        this.emailService = emailService;
        this.emailConstant = emailConstant;
        this.userReportRepository = userReportRepository;
        this.userDAO = userDAO;
    }

    @Override
    public UserOverviewData getUser(final String role, final String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!user.getStatus().equals(EUserStatus.ACTIVE)
                && !role.equals(ERole.ADMIN.name())
        ) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        return getUserFrom(user);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateUser(final String userId, final UserForm userForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException(message.getErrorUnauthorized()));
        experienceRepository.deleteAllByUser(user);
        userPositionRepository.deleteAllByUser(user);
        userFieldRepository.deleteAllByUser(user);
        userSkillRepository.deleteAllByUser(user);

        User userUpdate = DataBuilder.to(userForm, User.class);
        userUpdate.setId(user.getId());
        userUpdate.setEmail(user.getEmail());
        userUpdate.setPwd(user.getPwd());
        userUpdate.setAvatar(user.getAvatar());
        userUpdate.setCreatedAt(user.getCreatedAt());
        userUpdate.setUpdatedAt(TimeUtils.comNowDatetime());
        userUpdate.setStatus(user.getStatus());
        userUpdate.setRole(user.getRole());

        if (StringUtils.hasLength(userForm.getMajorId())) {
            Major major = majorRepository.findById(userForm.getMajorId())
                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
            userUpdate.setMajor(major);

        }
        userRepository.saveAndFlush(userUpdate);

        List<ExperienceForm> experienceForms = userForm.getExperiences();
        if (!CollectionUtils.isEmpty(experienceForms)) {
            experienceForms.forEach(experienceForm -> {
                Experience experience = DataBuilder.to(experienceForm, Experience.class);
                experience.setUser(userUpdate);
                experienceRepository.saveAndFlush(experience);
            });
        }
        List<String> positionIds = userForm.getPositionIds();
        if (!CollectionUtils.isEmpty(positionIds)) {
            positionIds.forEach(positionId -> {
                Position position = positionRepository.findById(positionId)
                        .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
                UserPosition userPosition = UserPosition.builder()
                        .position(position)
                        .user(userUpdate)
                        .build();
                userPositionRepository.saveAndFlush(userPosition);
            });
        }
        List<String> fieldIds = userForm.getFieldIds();
        if (!CollectionUtils.isEmpty(fieldIds)) {
            fieldIds.forEach(fieldId -> {
                Field field = fieldRepository.findById(fieldId)
                        .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
                UserField userField = UserField.builder()
                        .user(userUpdate)
                        .field(field)
                        .build();
                userFieldRepository.saveAndFlush(userField);
            });
        }

        List<String> techIds = userForm.getTechIds();
        if (!CollectionUtils.isEmpty(techIds)) {
            techIds.forEach(techId -> {
                Technology technology = technologyRepository.findById(techId)
                        .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
                UserSkill userSkill = UserSkill.builder()
                        .user(userUpdate)
                        .technology(technology)
                        .build();
                userSkillRepository.saveAndFlush(userSkill);
            });
        }
    }

    @Override
    public void updateUserStatus(final String userId, final String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        user.setStatus(EUserStatus.valueOf(status));
        user.setUpdatedAt(TimeUtils.comNowDatetime());
        userRepository.saveAndFlush(user);
    }

    @Override
    public UserOverviewData getUserByUsername(final String role, final String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
//        if (user.getRole().getId().equals(ERole.ADMIN)
//                || !user.getStatus().equals(EUserStatus.ACTIVE)
//                && !role.equals(ERole.ADMIN.name())
//        ) {
//            throw new ServerErrorException(message.getErrorUnauthorized());
//        }
        return getUserFrom(user);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String updateAvatar(final MultipartFile avatar, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        try {
            String fileName = user.getAvatar().substring(user.getAvatar().lastIndexOf("/") + 1);
            s3Component.delete("users/" + userId + "/avatar", fileName);
            String avatarUser = s3Component.upload("users/" + userId + "/avatar", avatar);
            user.setAvatar(avatarUser);
            userRepository.saveAndFlush(user);

            return avatarUser;
        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }
    }

    @Override
    public PagedResponse<HomeUserData> filterUser(final ERole role, final String search, final String positionId,
                                                  final String fieldId, final String skillId, final String status, final Integer page, final Integer size) {

        List<Position> positions = StringUtils.hasLength(positionId) ? positionRepository.findAllById(Arrays.asList(positionId.split(","))) : null;
        List<Field> fields = StringUtils.hasLength(fieldId) ? fieldRepository.findAllById(Arrays.asList(fieldId.split(","))) : null;
        List<Technology> skills = StringUtils.hasLength(skillId) ? technologyRepository.findAllById(Arrays.asList(skillId.split(","))) : null;

        long totalElements;
        List<User> users;
        if (ERole.ADMIN != role) {
            users = userDAO.findBySearchAndSkillAndFieldAndStatusAndPosition(search, skills, fields, AppConstant.DEFAULT_USER_STATUS, ERole.USER, positions, size, (page - 1) * size);
            totalElements = userDAO.countBySearchAndSkillAndFieldAndStatusAndPosition(search, skills, fields, AppConstant.DEFAULT_USER_STATUS, ERole.USER, positions);
        } else {
            users = userDAO.findBySearchAndSkillAndFieldAndStatusAndPosition(search, skills, fields, status, ERole.USER, positions, size, (page - 1) * size);
            totalElements = userDAO.countBySearchAndSkillAndFieldAndStatusAndPosition(search, skills, fields, status, ERole.USER, positions);
        }
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        List<HomeUserData> userDataList = users.stream()
                .map(user -> {
                    Major major = majorRepository.findByUser(user.getId());
                    User userData = userRepository.getById(user.getId());
                    List<CommonData> positionsDataList = userData.getUserPositions()
                            .stream()
                            .map(userPosition -> new CommonData(userPosition.getPosition().getId(), userPosition.getPosition().getName())).collect(Collectors.toList());

                    List<CommonData> fieldsDataList = userData.getUserFields()
                            .stream()
                            .map(userField -> new CommonData(userField.getField().getId(), userField.getField().getName())).collect(Collectors.toList());

                    return HomeUserData.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .avatar(user.getAvatar())
                            .status(user.getStatus().name())
                            .major(Objects.nonNull(major) ? major.getName() : null)
                            .numberOfReport(userReportRepository.countAllByTypeAndUser(EUserReportType.ACCUSED_USER, user))
                            .positions(positionsDataList)
                            .fields(fieldsDataList)
                            .build();
                }).collect(Collectors.toList());


        return new PagedResponse<>(userDataList, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean register(final UserRegisterForm userRegisterForm) {
        if (userRepository.existsByEmail(userRegisterForm.getEmail()))
            throw new ServerErrorException(message.getErrorExistEmail());

        String id = IDGenerator.generateID(userRepository, 10);

        Role role = roleRepository.findById(ERole.USER)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        User user = DataBuilder.to(userRegisterForm, User.class);
        user.setId(id);
        user.setPwd(encoder.encode(userRegisterForm.getPassword()));
        user.setStatus(EUserStatus.INACTIVE);
        user.setAvatar("https://fstartds.s3.ap-southeast-1.amazonaws.com/static/images/default_avatar.png");
        user.setCreatedAt(TimeUtils.comNowDatetime());
        user.setUpdatedAt(TimeUtils.comNowDatetime());
        user.setRole(role);

        User savedUser = userRepository.saveAndFlush(user);
        String hash = createUserVerification(savedUser);

        // send email verification
        String url = new StringBuilder(constant.getAppHost())
                .append(String.format(constant.getAppRedirectURI(), id, hash))
                .toString();

        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(userRegisterForm.getEmail());
        config.setSubject(emailConstant.getSUBJECT_VERIFY_ACCOUNT());
        config.setTemplate("verify-account");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", user.getLastName() + " " + user.getFirstName());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());
        templateModel.put("verifyEmail", url);
        templateModel.put("contactSupportLink", "http://fstart.begin.vn/contact");
        config.setTemplateModel(templateModel);

        try {
            emailService.sendEmail(Collections.singletonList(config));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String verifyEmail(final String userId, final String hash) {
        User user = userRepository.findByIdAndStatus(userId, EUserStatus.INACTIVE)
                .orElseThrow(() -> new ExistenceException(message.getWarnUserNotFound()));
        UserVerification userVerification = userVerificationRepository.findByUserAndHashAndStatus(user, hash, EUserVerificationStatus.ACTIVE)
                .orElse(null);

        if (Objects.nonNull(userVerification)) {
            LocalDateTime currentTime = TimeUtils.nowDatetime();
            LocalDateTime expireTime = TimeUtils.getDateTime(userVerification.getExpireTime(), TimeUtils.DTF_yyyyMMddHHmmss);

            if (expireTime.isAfter(currentTime)) {
                userVerification.setStatus(EUserVerificationStatus.INACTIVE);
                userVerificationRepository.saveAndFlush(userVerification);

                user.setStatus(EUserStatus.NEW_ACTIVE);
                user.setUpdatedAt(TimeUtils.comNowDatetime());
                userRepository.saveAndFlush(user);

                return EUserVerificationStatus.INACTIVE.name();
            } else {
                return EUserVerificationStatus.EXPIRED.name();
            }
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean sendVerifyCode(final String email) {
        User user = userRepository.findByEmailAndStatus(email, EUserStatus.INACTIVE)
                .orElseThrow(() -> new ExistenceException(message.getWarnUserNotFound()));
        List<UserVerification> inactiveUserVerifications = userVerificationRepository.findAllByUserAndStatus(user, EUserVerificationStatus.ACTIVE)
                .stream()
                .map(userVerification -> {
                    userVerification.setStatus(EUserVerificationStatus.INACTIVE);
                    return userVerification;
                }).collect(Collectors.toList());
        userVerificationRepository.saveAllAndFlush(inactiveUserVerifications);

        String hash = createUserVerification(user);

        // send email verification
        String url = new StringBuilder(constant.getAppHost())
                .append(String.format(constant.getAppRedirectURI(), user.getId(), hash))
                .toString();

        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(email);
        config.setSubject(emailConstant.getSUBJECT_VERIFY_ACCOUNT());
        config.setTemplate("verify-account");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", user.getLastName() + " " + user.getFirstName());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());
        templateModel.put("verifyEmail", url);
        templateModel.put("contactSupportLink", "http://fstart.begin.vn/contact");
        config.setTemplateModel(templateModel);

        try {
            emailService.sendEmail(Collections.singletonList(config));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean createSkillSet(final String positionsId, final String fieldsId, final String technologiesId, final String userId) {
        User user = userRepository.getById(userId);

        if (StringUtils.hasLength(technologiesId)) {
            List<Technology> technologies = technologyRepository.findByIdIn(Arrays.asList(technologiesId.split(",")));
            List<UserSkill> userSkills = technologies.stream()
                    .map(technology -> UserSkill.builder()
                            .user(user)
                            .technology(technology)
                            .build()).collect(Collectors.toList());
            userSkillRepository.saveAllAndFlush(userSkills);
        }
        if (StringUtils.hasLength(positionsId)) {
            List<Position> positions = positionRepository.findByIdIn(Arrays.asList(positionsId.split(",")));
            List<UserPosition> userPositions = positions.stream()
                    .map(position -> UserPosition.builder()
                            .user(user)
                            .position(position)
                            .build())
                    .collect(Collectors.toList());
            userPositionRepository.saveAllAndFlush(userPositions);
        }
        if (StringUtils.hasLength(fieldsId)) {
            List<Field> fields = fieldRepository.findByIdIn(Arrays.asList(fieldsId.split(",")));
            List<UserField> userFields = fields.stream()
                    .map(field -> UserField.builder()
                            .field(field)
                            .user(user)
                            .build())
                    .collect(Collectors.toList());
            userFieldRepository.saveAllAndFlush(userFields);

        }
        return true;
    }

    @Override
    public DataWrapper blockUser(final String roleId, final String userId, final String reason) {
        if (!ERole.ADMIN.name().equals(roleId)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        user.setStatus(EUserStatus.BLOCKED);
        user.setReason(reason);
        userRepository.saveAndFlush(user);

        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(user.getEmail());
        config.setSubject(emailConstant.getSUBJECT_BLOCK_USER());
        config.setTemplate("block-account");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", user.getLastName() + " " + user.getFirstName());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());
        templateModel.put("reason", reason);

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
    public DataWrapper unblockUser(final String roleId, final String userId) {
        if (!ERole.ADMIN.name().equals(roleId)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        user.setStatus(EUserStatus.ACTIVE);
        user.setReason("");
        userRepository.saveAndFlush(user);

        EmailMessageConfig config = new EmailMessageConfig();

        config.setTo(user.getEmail());
        config.setSubject(emailConstant.getSUBJECT_UNBLOCK_USER());
        config.setTemplate("unblock-account");
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("fullName", user.getLastName() + " " + user.getFirstName());
        templateModel.put("adminEmail", emailConstant.getEMAIL_ADMIN());

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
    public boolean deleteExperience(final Long experienceId, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!experienceRepository.existsByUser(user)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        experienceRepository.delete(experience);
        return true;
    }

    @Override
    public List<ExperienceData> getAllExperiencesByUserId(final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        List<Experience> experiences = experienceRepository.findAllByUser(user);
        List<ExperienceData> experienceDataList = experiences.stream()
                .map(experience -> ExperienceData.transform(experience))
                .collect(Collectors.toList());
        return experienceDataList;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createExperience(final ExperienceForm experienceForm, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Experience experience = DataBuilder.to(experienceForm, Experience.class);
        experience.setUser(user);
        experienceRepository.saveAndFlush(experience);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateExperience(final ExperienceForm experienceForm, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        Experience experience = experienceRepository.findById(experienceForm.getId())
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        if (!experienceRepository.existsByUser(user)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        experience = DataBuilder.to(experienceForm, Experience.class);
        experience.setUser(user);
        experienceRepository.saveAndFlush(experience);
    }

    /**
     * Private function
     */
    private UserOverviewData getUserFrom(User user) {
        List<Experience> experiences = experienceRepository.findAllByUser(user);
        List<UserField> userFields = userFieldRepository.findAllByUser(user);
        List<UserSkill> userSkills = userSkillRepository.findAllByUser(user);
        return UserOverviewData.transform(user, experiences, userFields, userSkills);
    }

    private String createUserVerification(User user) {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (userVerificationRepository.existsByHash(uuid));

        // create email verification
        String expireTime = TimeUtils.nowDatetime().plusMinutes(constant.getUserVerificationExpire())
                .format(DateTimeFormatter.ofPattern(TimeUtils.DTF_yyyyMMddHHmmss));
        UserVerification userVerification = UserVerification.builder()
                .hash(uuid)
                .user(user)
                .expireTime(expireTime)
                .status(EUserVerificationStatus.ACTIVE)
                .build();
        userVerificationRepository.saveAndFlush(userVerification);

        return uuid;
    }

}
