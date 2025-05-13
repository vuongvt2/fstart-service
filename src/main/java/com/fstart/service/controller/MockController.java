package com.fstart.service.controller;

import com.fstart.service.common.constant.AWSConstant;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.utils.IDGenerator;
import com.fstart.service.common.utils.TimeUtils;
import com.fstart.service.component.S3Component;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.*;
import com.fstart.service.repository.*;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockController
 *
 * @author VuongVT2
 * @since 2021/10/05
 */
@RestController
@RequestMapping(value = "/fs/api/v1/mock")
public class MockController {

    private final AWSConstant awsConstant;
    private final PasswordEncoder encoder;

    private final S3Component s3Component;

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FieldRepository fieldRepository;
    private final MajorRepository majorRepository;
    private final TechnologyRepository technologyRepository;
    private final CountryRepository countryRepository;
    private final ProjectRepository projectRepository;
    private final ProjectFieldRepository projectFieldRepository;
    private final UserFieldRepository userFieldRepository;
    private final UserSkillRepository userSkillRepository;
    private final ProjectTechnologyRepository projectTechnologyRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final DocumentRepository documentRepository;
    private final PositionRepository positionRepository;
    private final NewsRepository newsRepository;
    private final StartupRepository startupRepository;
    private final StartupFieldRepository startupFieldRepository;
    private final FounderRepository founderRepository;
    private final StartupFounderRepository startupFounderRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final ViolationRepository violationRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final UserPositionRepository userPositionRepository;
    private final DiscussionRepository discussionRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final UserInvitationRepository userInvitationRepository;
    private final InvitationRepository invitationRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ContactRepository contactRepository;
    private final UserReportRepository userReportRepository;
    private final ProjectReportRepository projectReportRepository;

    public MockController(final AWSConstant awsConstant,
                          final PasswordEncoder encoder,
                          final S3Component s3Component,
                          final ExperienceRepository experienceRepository,
                          final UserRepository userRepository,
                          final RoleRepository roleRepository,
                          final FieldRepository fieldRepository,
                          final MajorRepository majorRepository,
                          final TechnologyRepository technologyRepository,
                          final CountryRepository countryRepository,
                          final ProjectRepository projectRepository,
                          final ProjectFieldRepository projectFieldRepository,
                          final UserFieldRepository userFieldRepository,
                          final UserSkillRepository userSkillRepository,
                          final ProjectTechnologyRepository projectTechnologyRepository,
                          final ProjectTeamMemberRepository projectTeamMemberRepository,
                          final DocumentRepository documentRepository,
                          final PositionRepository positionRepository,
                          final NewsRepository newsRepository,
                          final StartupRepository startupRepository,
                          final StartupFieldRepository startupFieldRepository,
                          final FounderRepository founderRepository,
                          final StartupFounderRepository startupFounderRepository,
                          final EventRepository eventRepository,
                          final EventParticipantRepository eventParticipantRepository,
                          final ViolationRepository violationRepository,
                          final UserVerificationRepository userVerificationRepository,
                          final UserPositionRepository userPositionRepository,
                          final DiscussionRepository discussionRepository,
                          final CommentRepository commentRepository,
                          final ReportRepository reportRepository,
                          final UserInvitationRepository userInvitationRepository,
                          final InvitationRepository invitationRepository,
                          final ProjectPositionRepository projectPositionRepository,
                          final ContactRepository contactRepository,
                          final UserReportRepository userReportRepository,
                          final ProjectReportRepository projectReportRepository) {
        this.awsConstant = awsConstant;
        this.encoder = encoder;
        this.s3Component = s3Component;
        this.experienceRepository = experienceRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.fieldRepository = fieldRepository;
        this.majorRepository = majorRepository;
        this.technologyRepository = technologyRepository;
        this.countryRepository = countryRepository;
        this.projectRepository = projectRepository;
        this.projectFieldRepository = projectFieldRepository;
        this.userFieldRepository = userFieldRepository;
        this.userSkillRepository = userSkillRepository;
        this.projectTechnologyRepository = projectTechnologyRepository;
        this.projectTeamMemberRepository = projectTeamMemberRepository;
        this.documentRepository = documentRepository;
        this.positionRepository = positionRepository;
        this.newsRepository = newsRepository;
        this.startupRepository = startupRepository;
        this.startupFieldRepository = startupFieldRepository;
        this.founderRepository = founderRepository;
        this.startupFounderRepository = startupFounderRepository;
        this.eventRepository = eventRepository;
        this.eventParticipantRepository = eventParticipantRepository;
        this.violationRepository = violationRepository;
        this.userVerificationRepository = userVerificationRepository;
        this.userPositionRepository = userPositionRepository;
        this.discussionRepository = discussionRepository;
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
        this.userInvitationRepository = userInvitationRepository;
        this.invitationRepository = invitationRepository;
        this.projectPositionRepository = projectPositionRepository;
        this.contactRepository = contactRepository;
        this.userReportRepository = userReportRepository;
        this.projectReportRepository = projectReportRepository;
    }

    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initStatic(@RequestParam(value = "user", defaultValue = "0") @Positive int numberOfUser,
                                        @RequestParam(value = "admin", defaultValue = "0") @Positive int numberOfAdmin,
                                        @RequestParam(value = "project", defaultValue = "0") @Positive int numberOfProject,
                                        @RequestParam(value = "news", defaultValue = "0") @Positive int numberOfNews,
                                        @RequestParam(value = "startup", defaultValue = "0") @Positive int numberOfStartup,
                                        @RequestParam(value = "event", defaultValue = "0") @Positive int numberOfEvent,
                                        @RequestParam(value = "eventParticipant", defaultValue = "0") @Positive int numberOfEventParticipant,
                                        @RequestParam(value = "statusEventParticipant", defaultValue = "ACCEPTED") String statusEventParticipant
    ) throws IOException, URISyntaxException {
        userReportRepository.deleteAll();
        projectReportRepository.deleteAll();
        projectPositionRepository.deleteAll();
        userInvitationRepository.deleteAll();
        invitationRepository.deleteAll();
        reportRepository.deleteAll();
        violationRepository.deleteAll();
        userPositionRepository.deleteAll();
        newsRepository.deleteAll();
        startupFieldRepository.deleteAll();
        userSkillRepository.deleteAll();
        eventParticipantRepository.deleteAll();
        userVerificationRepository.deleteAll();
        projectFieldRepository.deleteAll();
        projectTechnologyRepository.deleteAll();
        userFieldRepository.deleteAll();
        projectTechnologyRepository.deleteAll();
        fieldRepository.deleteAll();
        technologyRepository.deleteAll();
        documentRepository.deleteAll();
        projectTeamMemberRepository.deleteAll();
        projectRepository.deleteAll();
        experienceRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        majorRepository.deleteAll();
        positionRepository.deleteAll();
        violationRepository.deleteAll();
        startupFounderRepository.deleteAll();
        founderRepository.deleteAll();
        startupRepository.deleteAll();
        eventRepository.deleteAll();

        // Mock static data
        MockJSONUtils.initStatic("fields.json", Field[].class, fieldRepository);
        MockJSONUtils.initStatic("technologies.json", Technology[].class, technologyRepository);
        MockJSONUtils.initStatic("roles.json", Role[].class, roleRepository);
        MockJSONUtils.initStatic("majors.json", Major[].class, majorRepository);
        MockJSONUtils.initStatic("countries.json", Country[].class, countryRepository);
        MockJSONUtils.initStatic("positions.json", Position[].class, positionRepository);
        MockJSONUtils.initStatic("violations.json", Violation[].class, violationRepository);

        // Prepare common data
        List<Technology> technologies = technologyRepository.findAll();
        List<Field> fields = fieldRepository.findAll();
        if (CollectionUtils.isEmpty(technologies) || CollectionUtils.isEmpty(fields))
            throw new ServerErrorException();

        // Mock user
        mockUser(numberOfUser, ERole.USER, fields, technologies);
        mockUser(numberOfAdmin, ERole.ADMIN, fields, technologies);

        // Mock group
        mockProjects(numberOfProject, fields, technologies);
        mockNews(numberOfNews);
        mockStartup(numberOfStartup);
        mockEvent(numberOfEvent);
        mockEventParticipant(numberOfEventParticipant, statusEventParticipant);

        return ResponseEntity.ok(userRepository.findAll()
                .stream()
                .map(user -> {
                    Map<String, String> results = new HashMap<>();
                    results.put("id", user.getId());
                    results.put("email", user.getEmail());
                    results.put("role", user.getRole().getId().name());

                    return results;
                })
                .collect(Collectors.toList()));
    }


    private void mockProjects(int numberOfProject, List<Field> fields, List<Technology> technologies) throws IOException, URISyntaxException {
        Faker faker = new Faker();

        Role userRole = roleRepository.findById(ERole.USER)
                .orElseThrow();

        List<User> users = userRepository.findByRole(userRole);
        if (CollectionUtils.isEmpty(users))
            throw new ServerErrorException();

//        String projectLogoLink = saveStaticToS3("default_logo.jpeg");

        List<String> existOwner = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        for (var i = 0; i < numberOfProject; i++) {

            String id = IDGenerator.generateID(projectRepository, 10);

            Project project = Project.builder()
                    .id(id)
                    .title(faker.lorem().sentence(5))
                    .subTitle(faker.lorem().sentence(10))
                    .description(faker.lorem().sentence(50))
                    .logo("https://fstartds.s3.ap-southeast-1.amazonaws.com/static/images/default_logo.jpeg")
                    .status(EProjectStatus.APPROVED)
                    .privacy(EProjectPrivacy.PUBLIC)
                    .createdAt(TimeUtils.comNowDatetime())
                    .updatedAt(TimeUtils.comNowDatetime())
                    .build();
            projectRepository.saveAndFlush(project);
            projects.add(project);

            int totalField = faker.random().nextInt(1, 5);
            for (var f = 0; f < totalField; f++) {
                ProjectField projectField = ProjectField.builder()
                        .project(project)
                        .field(fields.get(faker.random().nextInt(fields.size())))
                        .build();
                projectFieldRepository.saveAndFlush(projectField);
            }

            int totalTechnology = faker.random().nextInt(1, 5);
            for (var t = 0; t < totalTechnology; t++) {
                ProjectTechnology projectTechnology = ProjectTechnology.builder()
                        .project(project)
                        .technology(technologies.get(faker.random().nextInt(technologies.size())))
                        .build();
                projectTechnologyRepository.saveAndFlush(projectTechnology);
            }

            User owner;
            do {
                owner = users.get(faker.random().nextInt(users.size()));
            } while (existOwner.contains(owner.getId()));
            ProjectTeamMember projectTeamMember = ProjectTeamMember.builder()
                    .user(owner)
                    .position(positionRepository.getById("PO"))
                    .project(project)
                    .build();
            projectTeamMemberRepository.save(projectTeamMember);
            projectTeamMemberRepository.flush();

            for (int j = 0; j < faker.random().nextInt(5, 10); j++) {
                Document document = Document.builder()
                        .name(faker.lorem().words(faker.random().nextInt(3, 6)).stream().collect(Collectors.joining(" ")))
                        .project(project)
                        .link("s3://fstartds/documents/Phiếu-đăng-kí-khóa-luận-tốt-nghiệp_Spring22_HUONGNTC_AnhTT_FstartWeb.doc")
                        .build();
                documentRepository.saveAndFlush(document);
            }
        }
    }

    public void mockNews(int numberOfNews) {

        Faker faker = new Faker();

        Role userRole = roleRepository.findById(ERole.USER)
                .orElseThrow();

        List<User> users = userRepository.findByRole(userRole);
        if (CollectionUtils.isEmpty(users))
            throw new ServerErrorException();

        for (var i = 0; i < numberOfNews; i++) {

            String id = IDGenerator.generateID(newsRepository, 10);

            News news = News.builder()
                    .id(id)
                    .user(users.get(faker.random().nextInt(users.size())))
                    .createdAt(TimeUtils.comNowDatetime())
                    .updatedAt(TimeUtils.comNowDatetime())
                    .title(faker.lorem().sentence(5))
                    .shortDescription(faker.lorem().sentence(5))
                    .description(faker.lorem().sentence(50))
                    .thumbnail("https://i1-kinhdoanh.vnecdn.net/2022/04/13/oil4-1649815850-6107-1649815884.jpg?w=380&h=228&q=100&dpr=1&fit=crop&s=pVeWSJ-j-h7dq-wxNL86rg")
                    .build();
            newsRepository.save(news);

        }
    }

    public void mockStartup(int numberOfStartup) {
        {

            Faker faker = new Faker();

            List<Country> countries = countryRepository.findAll();
            List<Field> fields = fieldRepository.findAll();
            for (var i = 0; i < numberOfStartup; i++) {


                Startup startup = Startup.builder()
                        .id((long) i)
                        .country(countries.get(faker.random().nextInt(countries.size())))
                        .logo("https://media.istockphoto.com/photos/idea-and-startup-concept-picture-id1164602484?k=20&m=1164602484&s=612x612&w=0&h=hO3zCTxMaBbG-vR-y1kYifrADGzxYz1vxbsMLsPQsro=")
                        .description(faker.lorem().sentence(50))
                        .name(faker.name().name())
                        .originalLink("https://www.ycombinator.com/companies/")
                        .shortDescription(faker.lorem().sentence(5))
                        .status(EStartupStatus.ACQUIRED)
                        .startupSize(faker.random().nextInt(4, 100))
                        .founded(faker.random().nextInt(1999, 2021))
                        .build();

                startupRepository.save(startup);

                int totalField = faker.random().nextInt(1, 5);
                List<StartupField> startupFields = new ArrayList<>();
                for (var f = 0; f < totalField; f++) {
                    StartupField startupField = StartupField.builder()
                            .startup(startup)
                            .field(fields.get(faker.random().nextInt(fields.size())))
                            .build();
                    startupFields.add(startupField);
                }
                startupFieldRepository.saveAllAndFlush(startupFields);

                List<Founder> founders = new ArrayList<>();

                Founder founder = Founder.builder()
                        .id((long) i)
                        .name(faker.name().fullName())
                        .socialNetwork("https://www.facebook.com/vuongvt" + i)
                        .build();
                founders.add(founder);
                founderRepository.saveAllAndFlush(founders);

                StartupFounder startupFounder = StartupFounder.builder()
                        .founder(founders.get(faker.random().nextInt(founders.size())))
                        .startup(startup)
                        .build();
                startupFounderRepository.save(startupFounder);
            }
        }
    }

    public void mockEvent(int numberOfEvent) {
        {

            Faker faker = new Faker();
            for (var i = 0; i < numberOfEvent; i++) {
                String id = IDGenerator.generateID(eventRepository, 10);

                Event event = Event.builder()
                        .id(id)
                        .banner("https://media.istockphoto.com/photos/idea-and-startup-concept-picture-id1164602484?k=20&m=1164602484&s=612x612&w=0&h=hO3zCTxMaBbG-vR-y1kYifrADGzxYz1vxbsMLsPQsro=")
                        .createdAt(TimeUtils.comNowDatetime())
                        .updatedAt(TimeUtils.comNowDatetime())
                        .description(faker.lorem().sentence(50))
                        .title(faker.lorem().sentence(1))
                        .startTime("20220413143918")
                        .endTime("20220613143918")
                        .build();
                eventRepository.save(event);
                String idEventEndTime = IDGenerator.generateID(eventRepository, 10);
                Event eventEndTime = Event.builder()
                        .id(idEventEndTime)
                        .banner("https://media.istockphoto.com/photos/idea-and-startup-concept-picture-id1164602484?k=20&m=1164602484&s=612x612&w=0&h=hO3zCTxMaBbG-vR-y1kYifrADGzxYz1vxbsMLsPQsro=")
                        .createdAt(TimeUtils.comNowDatetime())
                        .updatedAt(TimeUtils.comNowDatetime())
                        .description(faker.lorem().sentence(50))
                        .title(faker.lorem().sentence(1))
                        .startTime("20220213143918")
                        .endTime("20220314143918")
                        .build();
                eventRepository.save(eventEndTime);

                String idEventFuture = IDGenerator.generateID(eventRepository, 10);

                Event eventFuture = Event.builder()
                        .id(idEventFuture)
                        .banner("https://media.istockphoto.com/photos/idea-and-startup-concept-picture-id1164602484?k=20&m=1164602484&s=612x612&w=0&h=hO3zCTxMaBbG-vR-y1kYifrADGzxYz1vxbsMLsPQsro=")
                        .createdAt(TimeUtils.comNowDatetime())
                        .updatedAt(TimeUtils.comNowDatetime())
                        .description(faker.lorem().sentence(50))
                        .title(faker.lorem().sentence(1))
                        .startTime("20220613143918")
                        .endTime("20220914143918")
                        .build();
                eventRepository.save(eventFuture);

            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init-event-participant", produces = MediaType.APPLICATION_JSON_VALUE)
    public void mockEventParticipant(int numberOfEventParticipant, String statusEventParticipant) {
        {
            List<Event> events = eventRepository.findAll();
            List<Project> projects = projectRepository.findAll();

            Faker faker = new Faker();
            for (int i = 0; i < numberOfEventParticipant; i++) {

                for (Event event : events) {
                    EventParticipant eventParticipant = EventParticipant.builder()
                            .status(EEventParticipantStatus.valueOf(statusEventParticipant))
                            .project(projects.get(i))
                            .event(event)
                            .createdAt(TimeUtils.comNowDatetime())
                            .updatedAt(TimeUtils.comNowDatetime())
                            .build();
                    eventParticipantRepository.save(eventParticipant);
                }

                String idEventEndTime = IDGenerator.generateID(eventRepository, 10);
                Event eventEndTime = Event.builder()
                        .id(idEventEndTime)
                        .banner("https://media.istockphoto.com/photos/idea-and-startup-concept-picture-id1164602484?k=20&m=1164602484&s=612x612&w=0&h=hO3zCTxMaBbG-vR-y1kYifrADGzxYz1vxbsMLsPQsro=")
                        .createdAt(TimeUtils.comNowDatetime())
                        .updatedAt(TimeUtils.comNowDatetime())
                        .description(faker.lorem().sentence(50))
                        .title(faker.lorem().sentence(1))
                        .startTime("20220213143918")
                        .endTime("20220314143918")
                        .build();
                eventRepository.save(eventEndTime);

                String idEventFuture = IDGenerator.generateID(eventRepository, 10);
                Event eventFuture = Event.builder()
                        .id(idEventFuture)
                        .banner("https://media.istockphoto.com/photos/idea-and-startup-concept-picture-id1164602484?k=20&m=1164602484&s=612x612&w=0&h=hO3zCTxMaBbG-vR-y1kYifrADGzxYz1vxbsMLsPQsro=")
                        .createdAt(TimeUtils.comNowDatetime())
                        .updatedAt(TimeUtils.comNowDatetime())
                        .description(faker.lorem().sentence(50))
                        .title(faker.lorem().sentence(1))
                        .startTime("20220613143918")
                        .endTime("20220914143918")
                        .build();
                eventRepository.save(eventFuture);

            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init-contact", produces = MediaType.APPLICATION_JSON_VALUE)
    public void mockContact(@RequestParam(value = "contactNew", defaultValue = "0") @Positive int numberOfContactNew,
                            @RequestParam(value = "contactResolved", defaultValue = "0") @Positive int numberOfContactResolved) {
        {
            contactRepository.deleteAll();

            Faker faker = new Faker();
            for (var i = 0; i < numberOfContactNew; i++) {
                Contact contact = Contact.builder()
                        .createdAt(TimeUtils.comNowDatetime())
                        .email(faker.internet().emailAddress())
                        .fullName(faker.name().fullName())
                        .status(EContactStatus.NEW)
                        .message(faker.lorem().sentence(3))
                        .build();
                contactRepository.save(contact);
            }
            for (var i = 0; i < numberOfContactResolved; i++) {
                Contact contact = Contact.builder()
                        .createdAt(TimeUtils.comNowDatetime())
                        .email(faker.internet().emailAddress())
                        .fullName(faker.name().fullName())
                        .status(EContactStatus.RESOLVED)
                        .message(faker.lorem().sentence(3))
                        .build();
                contactRepository.save(contact);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init-discussion", produces = MediaType.APPLICATION_JSON_VALUE)
    public void mockDiscussion() {
        {
            List<Project> projects = projectRepository.findAll();

            Faker faker = new Faker();

            projects.forEach(project -> {
                List<ProjectTeamMember> projectTeamMembers = (List<ProjectTeamMember>) project.getProjectTeamMembers();
                for (int j = 0; j < 2; j++) {
                    Discussion discussion = Discussion.builder()
                            .project(project)
                            .createdAt(TimeUtils.comNowDatetime())
                            .updatedAt(TimeUtils.comNowDatetime())
                            .user(projectTeamMembers.get(faker.random().nextInt(projectTeamMembers.size())).getUser())
                            .status(EDiscussionStatus.NEW)
                            .content(faker.lorem().sentence(1))
                            .build();
                    discussionRepository.save(discussion);
                }
                for (int j = 0; j < 3; j++) {
                    Discussion discussion = Discussion.builder()
                            .project(project)
                            .createdAt(TimeUtils.comNowDatetime())
                            .updatedAt(TimeUtils.comNowDatetime())
                            .user(projectTeamMembers.get(faker.random().nextInt(projectTeamMembers.size())).getUser())
                            .status(EDiscussionStatus.COMMENTED)
                            .content(faker.lorem().sentence(1))
                            .build();
                    discussionRepository.save(discussion);

                    for (int k = 0; k < 5; k++) {
                        Comment comment = Comment.builder()
                                .createdAt(TimeUtils.comNowDatetime())
                                .updatedAt(TimeUtils.comNowDatetime())
                                .content(faker.lorem().sentence(1))
                                .user(projectTeamMembers.get(faker.random().nextInt(projectTeamMembers.size())).getUser())
                                .discussion(discussion)
                                .build();
                        commentRepository.save(comment);
                    }
                }

            });

        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public void mockReport() {

        reportRepository.deleteAll();

        Role userRole = roleRepository.findById(ERole.USER)
                .orElseThrow();
        Faker faker = new Faker();

        List<Violation> violations = violationRepository.findAll();
        List<User> users = userRepository.findByRole(userRole);
        List<Project> projects = projectRepository.findAll();

        int size = users.size() - 30;
        if (users.size() > 106) {
            size = 60;
        }
        for (int i = 0; i < size / 2; i++) {
            for (int j = i; j < i + 5; j++) {
                Report report = Report.builder()
                        .updatedAt(TimeUtils.comNowDatetime())
                        .createdAt(TimeUtils.comNowDatetime())
                        .status(EReportStatus.NEW)
                        .violation(violations.get(faker.random().nextInt(0, 4)))
                        .build();
                report = reportRepository.saveAndFlush(report);
                User accuser = users.get(j);
                UserReport userReport1 = UserReport.builder()
                        .report(report)
                        .user(accuser)
                        .type(EUserReportType.ACCUSER)
                        .build();
                userReportRepository.saveAndFlush(userReport1);
                User reported = users.get(i + 6);
                UserReport userReport2 = UserReport.builder()
                        .report(report)
                        .user(reported)
                        .type(EUserReportType.ACCUSED_USER)
                        .build();
                userReportRepository.saveAndFlush(userReport2);

            }

        }

        for (int i = size / 2; i < size; i++) {
            for (int j = i; j < i + 5; j++) {
                Report report = Report.builder()
                        .updatedAt(TimeUtils.comNowDatetime())
                        .createdAt(TimeUtils.comNowDatetime())
                        .status(EReportStatus.NEW)
                        .violation(violations.get(5))
                        .reason(faker.lorem().words(faker.random().nextInt(5, 10)).stream().collect(Collectors.joining(" ")))
                        .build();
                report = reportRepository.saveAndFlush(report);
                User accuser = users.get(j);
                UserReport userReport1 = UserReport.builder()
                        .report(report)
                        .user(accuser)
                        .type(EUserReportType.ACCUSER)
                        .build();
                userReportRepository.saveAndFlush(userReport1);
                User reported = users.get(i + 6);
                UserReport userReport2 = UserReport.builder()
                        .report(report)
                        .user(reported)
                        .type(EUserReportType.ACCUSED_USER)
                        .build();
                userReportRepository.saveAndFlush(userReport2);

            }

        }
        //init report project
        size = projects.size() - 15;
        for (int i = 0; i < size / 2; i++) {
            for (int j = i; j < i + 5; j++) {
                Report report = Report.builder()
                        .updatedAt(TimeUtils.comNowDatetime())
                        .createdAt(TimeUtils.comNowDatetime())
                        .status(EReportStatus.NEW)
                        .violation(violations.get(faker.random().nextInt(0, 4)))
                        .build();
                report = reportRepository.saveAndFlush(report);
                Project project = projects.get(j);
                User accuser = users.get(j);
                if (!projectTeamMemberRepository.existsByProjectAndUser(project, accuser)) {
                    ProjectReport projectReport = ProjectReport.builder()
                            .user(accuser)
                            .report(report)
                            .project(project)
                            .build();
                    projectReportRepository.saveAndFlush(projectReport);
                }

            }

        }

        for (int i = size / 2; i < size; i++) {
            for (int j = i; j < i + 5; j++) {
                Report report = Report.builder()
                        .updatedAt(TimeUtils.comNowDatetime())
                        .createdAt(TimeUtils.comNowDatetime())
                        .status(EReportStatus.NEW)
                        .reason(faker.lorem().words(faker.random().nextInt(5, 10)).stream().collect(Collectors.joining(" ")))
                        .violation(violations.get(5))
                        .build();
                report = reportRepository.saveAndFlush(report);
                Project project = projects.get(j);
                User accuser = users.get(j);
                if (!projectTeamMemberRepository.existsByProjectAndUser(project, accuser)) {
                    ProjectReport projectReport = ProjectReport.builder()
                            .user(accuser)
                            .report(report)
                            .project(project)
                            .build();
                    projectReportRepository.saveAndFlush(projectReport);
                }

            }

        }

    }

    /**
     * Private Function
     */
    private void mockUser(int numberOf, ERole eRole, List<Field> fields, List<Technology> technologies) throws IOException, URISyntaxException {
        if (numberOf <= 0)
            return;

        Role role = roleRepository.findById(eRole)
                .orElseThrow(() -> new ServerErrorException());

        List<Major> majors = majorRepository.findAll();

        Faker faker = new Faker();
        for (var i = 0; i < numberOf; i++)
            mockUer(faker, faker.internet().emailAddress(), eRole, role, majors, fields, technologies);

//        if (ERole.USER == eRole) {
//            mockUer(faker, "VuongVT2010698@gmail.com", eRole, role, majors, fields, technologies);
//        }
    }

    private User mockUer(Faker faker, String mail, ERole eRole, Role role, List<Major> majors, List<Field> fields, List<Technology> technologies) throws IOException, URISyntaxException {
        String id = IDGenerator.generateID(userRepository, 10);
        String time = TimeUtils.comNowDatetime();

        Major major = majors.get(faker.random().nextInt(majors.size()));

        User user = User.builder()
                .id(id)
                .email(mail)
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .bio(faker.lorem().sentence(20))
                .pwd(encoder.encode("password"))
                .phoneNumber(faker.phoneNumber().cellPhone().substring(12))
                .avatar("https://fstartds.s3.ap-southeast-1.amazonaws.com/static/images/default_avatar.png")
                .status(EUserStatus.ACTIVE)
                .address(faker.address().fullAddress())
                .createdAt(time)
                .updatedAt(time)
                .major(ERole.ADMIN.equals(eRole) ? null : major)
                .role(role)
                .build();
        userRepository.saveAndFlush(user);

        if (ERole.ADMIN != role.getId()) {
            List<Position> positions = positionRepository.findAll();
            UserPosition userPosition = UserPosition.builder()
                    .user(user)
                    .position(positions.get(faker.random().nextInt(positions.size())))
                    .build();
            userPositionRepository.save(userPosition);
            List<Position> positionsList = positions.stream()
                    .filter(p -> !p.equals(userPosition.getPosition()))
                    .collect(Collectors.toList());
            UserPosition userPosition2 = UserPosition.builder()
                    .user(user)
                    .position(positions.get(faker.random().nextInt(positionsList.size())))
                    .build();
            userPositionRepository.save(userPosition2);
        }
        int totalField = faker.random().nextInt(1, 5);
        for (var t = 0; t < totalField; t++) {
            UserField userField = UserField.builder()
                    .user(user)
                    .field(fields.get(faker.random().nextInt(fields.size())))
                    .build();
            userFieldRepository.saveAndFlush(userField);
        }

        int totalTechnology = faker.random().nextInt(1, 5);
        for (var t = 0; t < totalTechnology; t++) {
            UserSkill userSkill = UserSkill.builder()
                    .user(user)
                    .technology(technologies.get(faker.random().nextInt(technologies.size())))
                    .build();
            userSkillRepository.saveAndFlush(userSkill);
        }

        Experience experience = Experience.builder()
                .title(faker.lorem().sentence(1))
                .company(faker.company().name())
                .address(faker.address().fullAddress())
                .description(faker.lorem().sentence(5))
                .startWorking(TimeUtils.comNowDate())
                .endWorking(TimeUtils.comNowDate())
                .currentWorkingFlg(true)
                .user(user)
                .build();
        experienceRepository.saveAndFlush(experience);
        return user;
    }

    private String saveStaticToS3(String fileName) throws IOException, URISyntaxException {
        InputStream inputStream = MockController.class.getClassLoader().getResourceAsStream("static/images/" + fileName);
        File file = new File(awsConstant.getAwsS3TempPath() + "static/images/" + fileName);
        FileUtils.copyInputStreamToFile(inputStream, file);
        String endpoint = s3Component.upload("static/images", file);
        file.delete();

        return endpoint;
    }
}

class MockJSONUtils<S> {
    public static <S> void initStatic(String fileName, Class<S[]> clazz, JpaRepository jpa) throws FileNotFoundException {
        saveFromJson(fileName, clazz, jpa);
    }

    public static void delete(JpaRepository jpa) {
        jpa.deleteAll();
    }

    public static <S> void saveAll(JpaRepository jpa, List<S> entities) {
        jpa.saveAll(entities);
        jpa.flush();
    }

    public static <S> void saveFromJson(String fileName, Class<S[]> clazz, JpaRepository jpa) throws FileNotFoundException {
        jpa.saveAll(getFromJson(fileName, clazz));
        jpa.flush();
    }

    public static <S> List<S> getFromJson(String fileName, Class<S[]> clazz) throws FileNotFoundException {
        Gson gson = new Gson();
        InputStream inputStream = MockJSONUtils.class.getClassLoader().getResourceAsStream("static/json/" + fileName);

        S[] entities = gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), clazz);

        return Arrays.asList(entities);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class DistrictJSON {
    private String id;
    private String name;
    private String provinceId;
}