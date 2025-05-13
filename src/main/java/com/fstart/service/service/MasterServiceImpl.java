package com.fstart.service.service;

import com.fstart.service.common.constant.Message;
import com.fstart.service.common.exception.ExistenceException;
import com.fstart.service.common.exception.ServerErrorException;
import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.entity.*;
import com.fstart.service.enumeration.*;
import com.fstart.service.model.common.*;
import com.fstart.service.model.event.BaseEventData;
import com.fstart.service.model.home.HomeNewsData;
import com.fstart.service.model.home.HomeProjectData;
import com.fstart.service.model.home.HomeStartupData;
import com.fstart.service.model.home.HomeUserData;
import com.fstart.service.model.project.ProjectRecommendationData;
import com.fstart.service.model.user.UserRecommendationData;
import com.fstart.service.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MasterServiceImpl
 *
 * @author VuongVT2
 * @since 2022/04/11
 */
@Service
public class MasterServiceImpl implements MasterService {

    private final Message message;

    private final RoleRepository roleRepository;
    private final FieldRepository fieldRepository;
    private final PositionRepository positionRepository;
    private final TechnologyRepository technologyRepository;
    private final ProjectRepository projectRepository;
    private final ProjectFieldRepository projectFieldRepository;
    private final ProjectTechnologyRepository projectTechnologyRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ProjectTeamMemberRepository projectTeamMemberRepository;
    private final NewsRepository newsRepository;
    private final StartupRepository startupRepository;
    private final ViolationRepository violationRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserFieldRepository userFieldRepository;
    private final UserSkillRepository userSkillRepository;
    private final MajorRepository majorRepository;
    private final ReportRepository reportRepository;
    private final CountryRepository countryRepository;
    private final ProjectPositionRepository projectPositionRepository;

    public MasterServiceImpl(final Message message,
                             final RoleRepository roleRepository,
                             final FieldRepository fieldRepository,
                             final PositionRepository positionRepository,
                             final TechnologyRepository technologyRepository,
                             final ProjectRepository projectRepository,
                             final ProjectFieldRepository projectFieldRepository,
                             final ProjectTechnologyRepository projectTechnologyRepository,
                             final UserRepository userRepository,
                             final EventRepository eventRepository,
                             final ProjectTeamMemberRepository projectTeamMemberRepository,
                             final NewsRepository newsRepository,
                             final StartupRepository startupRepository,
                             final ViolationRepository violationRepository,
                             final UserPositionRepository userPositionRepository,
                             final UserFieldRepository userFieldRepository,
                             final UserSkillRepository userSkillRepository,
                             final MajorRepository majorRepository,
                             final ReportRepository reportRepository,
                             final CountryRepository countryRepository,
                             final ProjectPositionRepository projectPositionRepository) {
        this.message = message;
        this.roleRepository = roleRepository;
        this.fieldRepository = fieldRepository;
        this.positionRepository = positionRepository;
        this.technologyRepository = technologyRepository;
        this.projectRepository = projectRepository;
        this.projectFieldRepository = projectFieldRepository;
        this.projectTechnologyRepository = projectTechnologyRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.projectTeamMemberRepository = projectTeamMemberRepository;
        this.newsRepository = newsRepository;
        this.startupRepository = startupRepository;
        this.violationRepository = violationRepository;
        this.userPositionRepository = userPositionRepository;
        this.userFieldRepository = userFieldRepository;
        this.userSkillRepository = userSkillRepository;
        this.majorRepository = majorRepository;
        this.reportRepository = reportRepository;
        this.countryRepository = countryRepository;
        this.projectPositionRepository = projectPositionRepository;
    }

    @Override
    public HomeData getHomeData() {
        Pageable pageable = PageRequest.of(0, 10);

        Role role = roleRepository.findById(ERole.USER)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData()));

        List<HomeUserData> users = userRepository.findAllByRoleAndStatusOrderByUpdatedAtDesc(role, EUserStatus.ACTIVE, pageable)
                .stream()
                .map(user -> {
                    List<CommonData> positionsDataList = user.getUserPositions()
                            .stream()
                            .map(userPosition -> new CommonData(userPosition.getPosition().getId(), userPosition.getPosition().getName())).collect(Collectors.toList());
                    return HomeUserData.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .avatar(user.getAvatar())
                            .major(Objects.nonNull(user.getMajor()) ? user.getMajor().getName() : null)
                            .positions(positionsDataList)
                            .build();
                }).collect(Collectors.toList());

        List<HomeProjectData> projects = projectRepository.findAllByStatusOrderByUpdatedAtDesc(EProjectStatus.APPROVED, pageable)
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
                            .numberOfCurrentMember(numberOfCurrentMember)
                            .numberOfMember(numberOfMember + numberOfCurrentMember)
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

        Pageable pageableNews = PageRequest.of(0, 9);
        List<HomeNewsData> newsDataList = newsRepository.getAllNews(pageableNews).stream()
                .map(news -> {
//                    List<TagData> tags = DataBuilder.toList(Arrays.asList(news.getTags().toArray()), TagData.class);
                    return HomeNewsData.builder()
                            .id(news.getId())
                            .title(news.getTitle())
                            .shortDescription(news.getShortDescription())
                            .thumbnail(news.getThumbnail())
//                            .tags(tags)
                            .build();
                }).collect(Collectors.toList());

        List<HomeStartupData> homeStartupDataList = startupRepository.getTopStartup(pageable).stream()
                .map(startup -> DataBuilder.to(startup, HomeStartupData.class)).collect(Collectors.toList());

        List<BaseEventData> baseEventDataList = eventRepository.getTopEvent(pageable).stream()
                .map(event -> DataBuilder.to(event, BaseEventData.class)).collect(Collectors.toList());

        return HomeData.builder()
                .users(users)
                .projects(projects)
                .news(newsDataList)
                .startups(homeStartupDataList)
                .events(baseEventDataList)
                .build();
    }

    @Override
    public List<CommonData> getAllViolations() {
        List<Violation> violations = violationRepository.findAll();
        List<CommonData> violationDataList = new ArrayList<>();
        violations.forEach(violation -> {
            CommonData violationData = DataBuilder.to(violation, CommonData.class);
            violationData.setId(violation.getId().toString());
            violationDataList.add(violationData);
        });
        return violationDataList;
    }

    @Override
    public List<ProjectRecommendationData> getRecommendationForUser(final String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ServerErrorException(message.getErrorUnauthorized()));
        List<Position> userPositions = userPositionRepository.findAllByUser(user)
                .stream()
                .map(UserPosition::getPosition)
                .collect(Collectors.toList());
        List<Field> userFields = userFieldRepository.findAllByUser(user)
                .stream()
                .map(UserField::getField)
                .collect(Collectors.toList());
        List<Technology> userSkills = userSkillRepository.findAllByUser(user)
                .stream()
                .map(UserSkill::getTechnology)
                .collect(Collectors.toList());

        List<Project> projectMatchedListWithPosition = projectRepository.getProjectsByPosition(userPositions);
        List<Project> projectMatchedListWithSkill = projectRepository.getProjectsBySkill(userSkills);
        List<Project> projectMatchedListWithField = projectRepository.getProjectsByField(userFields);

        int totalUserTraits = userPositions.size() + userFields.size() + userSkills.size();

        List<ProjectRecommendationData> matchedProjectList = getMatchedProjects(projectMatchedListWithPosition, userPositions, userFields, userSkills, totalUserTraits);
        matchedProjectList = Stream.concat(getMatchedProjects(projectMatchedListWithField, userPositions, userFields, userSkills, totalUserTraits).stream(), matchedProjectList.stream()).collect(Collectors.toList());
        matchedProjectList = Stream.concat(getMatchedProjects(projectMatchedListWithSkill, userPositions, userFields, userSkills, totalUserTraits).stream(), matchedProjectList.stream()).collect(Collectors.toList());

        matchedProjectList = matchedProjectList.stream().distinct().sorted(Comparator.comparing(ProjectRecommendationData::getPercentMatched).reversed()).limit(10).collect(Collectors.toList());
        List<ProjectRecommendationData> recommendationList = new ArrayList<>();

        for (int i = 0; i < matchedProjectList.size(); i++) {

            ProjectRecommendationData tempProject = matchedProjectList.get(i);
            ProjectRecommendationData projectRecommendationData = recommendationList.stream()
                    .filter(item -> item.getId().equals(tempProject.getId()))
                    .findFirst()
                    .orElse(null);

            if (Objects.isNull(projectRecommendationData)) {
                recommendationList.add(tempProject);
            }
        }
        return recommendationList;
    }

    @Override
    public MasterData getAllFieldsAndTechnologiesAndPositions() {
        List<CommonData> positions = DataBuilder.toList(positionRepository.findByIdNotIn(Arrays.asList(EPosition.PO.name())), CommonData.class);
        List<CommonData> technologies = DataBuilder.toList(technologyRepository.findAll(), CommonData.class);
        List<CommonData> fields = DataBuilder.toList(fieldRepository.findAll(), CommonData.class);
        List<CommonData> majors = DataBuilder.toList(majorRepository.findAll(), CommonData.class);

        return MasterData.builder()
                .positions(positions)
                .fields(fields)
                .technologies(technologies)
                .majors(majors)
                .build();
    }

    @Override
    public List<UserRecommendationData> getRecommendationForProject(final String projectId, final String userId) {

        Project project = projectRepository.getById(projectId);
        User user = userRepository.getById(userId);
        List<UserRecommendationData> recommendationList = new ArrayList<>();
        boolean isProductOwner = projectTeamMemberRepository.existsByProjectAndUserAndPosition(project, user, positionRepository.getById(EPosition.PO.name()));
        List<UserRecommendationData> matchedUserList = new ArrayList<>();
        if (isProductOwner) {

            List<Position> projectPositions = projectTeamMemberRepository.findAllByProjectAndUserIsNull(project)
                    .stream()
                    .map(ProjectTeamMember::getPosition)
                    .collect(Collectors.toList());
            List<Field> projectFields = projectFieldRepository.findByProject(project)
                    .stream()
                    .map(ProjectField::getField)
                    .collect(Collectors.toList());
            List<Technology> projectTechnologies = projectTechnologyRepository.findByProject(project)
                    .stream()
                    .map(ProjectTechnology::getTechnology)
                    .collect(Collectors.toList());

            List<User> userMatchedListWithPosition = userRepository.getUsersByPosition(projectPositions);
            List<User> userMatchedListWithField = userRepository.getUsersByField(projectFields);
            List<User> userMatchedListWithTechnology = userRepository.getUsersByTechnology(projectTechnologies);

            int totalProjectTraits = projectPositions.size() + projectFields.size() + projectTechnologies.size();
            matchedUserList = getMatchedUsers(userMatchedListWithPosition, projectPositions, projectFields, projectTechnologies, totalProjectTraits);
            matchedUserList = Stream.concat(getMatchedUsers(userMatchedListWithField, projectPositions, projectFields, projectTechnologies, totalProjectTraits).stream(), matchedUserList.stream()).collect(Collectors.toList());
            matchedUserList = Stream.concat(getMatchedUsers(userMatchedListWithTechnology, projectPositions, projectFields, projectTechnologies, totalProjectTraits).stream(), matchedUserList.stream()).collect(Collectors.toList());

            matchedUserList = matchedUserList.stream()
                    .distinct()
                    .sorted(Comparator.comparing(UserRecommendationData::getPercentMatched).reversed()).limit(8)
                    .collect(Collectors.toList());



            for (int i = 0; i < matchedUserList.size(); i++) {

                UserRecommendationData tempUser = matchedUserList.get(i);
                UserRecommendationData userRecommendationData = recommendationList.stream()
                        .filter(item -> item.getId().equals(tempUser.getId()))
                        .findFirst()
                        .orElse(null);

                if (Objects.isNull(userRecommendationData)) {
                    recommendationList.add(tempUser);
                }
            }
        }
        return recommendationList;
    }

    @Override
    public StatisticalData getStatistical(final String role) {
        if (!ERole.ADMIN.name().equals(role)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        StatisticalUser user = StatisticalUser.builder()
                .numberOfUsers(userRepository.countAll())
                .numberOfNewActive(userRepository.countAllByStatus(EUserStatus.NEW_ACTIVE))
                .numberOfActive(userRepository.countAllByStatusAndRole(EUserStatus.ACTIVE, roleRepository.getById(ERole.USER)))
                .numberOfInactive(userRepository.countAllByStatus(EUserStatus.INACTIVE))
                .numberOfBlocked(userRepository.countAllByStatus(EUserStatus.BLOCKED))
                .build();

        StatisticalProject project = StatisticalProject.builder()
                .numberOfProjects(projectRepository.countAll())
                .numberOfPending(projectRepository.countAllByStatus(EProjectStatus.PENDING))
                .numberOfApprove(projectRepository.countAllByStatus(EProjectStatus.APPROVED))
                .numberOfReject(projectRepository.countAllByStatus(EProjectStatus.REJECTED))
                .numberOfBlocked(projectRepository.countAllByStatus(EProjectStatus.BLOCKED))
                .build();

        StatisticalReport report = StatisticalReport.builder()
                .numberOfReport(reportRepository.countAll())
                .numberOfNew(reportRepository.countAllByStatus(EReportStatus.NEW))
                .numberOfReject(reportRepository.countAllByStatus(EReportStatus.REJECTED))
                .numberOfAccepted(reportRepository.countAllByStatus(EReportStatus.ACCEPTED))
                .build();

        return StatisticalData.builder()
                .user(user)
                .project(project)
                .report(report)
                .build();
    }

    @Override
    public List<CommonData> getAllCountries() {
        return DataBuilder.toList(countryRepository.findAll(), CommonData.class);
    }

    /**
     * PRIVATE FUNCTION
     */

    private List<ProjectRecommendationData> getMatchedProjects(List<Project> matchedProjects, List<Position> userPositions, List<Field> userFields, List<Technology> userSkills, int totalUserTraits) {
        double percentMatched = 0;

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        ProjectRecommendationData recommendationData;

        List<ProjectRecommendationData> matchedProjectList = new ArrayList<>();
        for (int i = 0; i < matchedProjects.size(); i++) {
            int matchedCount = 0;
            for (int p = 0; p < userPositions.size(); p++) {
                if (matchedProjects.get(i).getProjectTeamMembers().stream().map(ProjectTeamMember::getPosition).collect(Collectors.toList()).contains(userPositions.get(p))) {
                    matchedCount += 1;
                }
            }

            for (int p = 0; p < userFields.size(); p++) {
                if (matchedProjects.get(i).getProjectFields().stream().map(ProjectField::getField).collect(Collectors.toList()).contains(userFields.get(p))) {
                    matchedCount += 1;
                }
            }

            for (int p = 0; p < userSkills.size(); p++) {
                if (matchedProjects.get(i).getProjectTechnologies().stream().map(ProjectTechnology::getTechnology).collect(Collectors.toList()).contains(userSkills.get(p))) {
                    matchedCount += 1;
                }
            }

            percentMatched = Double.parseDouble(decimalFormat.format((matchedCount / (totalUserTraits * 1.0)) * 100));
            List<Long> availableSlots = projectPositionRepository.getListAvailableSlotProject(matchedProjects.get(i));
            int numberOfMember = 0;
            for (Long availableSlot : availableSlots) {
                numberOfMember += availableSlot;
            }
            recommendationData = ProjectRecommendationData.transform(matchedProjects.get(i),
                    projectFieldRepository.findByProject(matchedProjects.get(i)), projectTechnologyRepository.findByProject(matchedProjects.get(i)));
            recommendationData.setNumberOfMember(projectTeamMemberRepository.countAllByProjectAndUserIsNotNull(matchedProjects.get(i)) + numberOfMember);
            recommendationData.setNumberOfCurrentMember(projectTeamMemberRepository.countAllByProject(matchedProjects.get(i)));
            recommendationData.setPercentMatched(percentMatched);
            matchedProjectList.add(recommendationData);
        }
        return matchedProjectList;
    }

    private List<UserRecommendationData> getMatchedUsers(List<User> matchedUsers, List<Position> projectPositions, List<Field> projectFields, List<Technology> projectTechnologies, int totalProjectTraits) {
        double percentMatched = 0;

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        UserRecommendationData recommendationData;

        List<UserRecommendationData> matchedUserList = new ArrayList<>();

        for (int i = 0; i < matchedUsers.size(); i++) {
            int matchedCount = 0;
            for (int p = 0; p < projectPositions.size(); p++) {
                if (matchedUsers.get(i).getUserPositions().stream().map(UserPosition::getPosition).collect(Collectors.toList()).contains(projectPositions.get(p))) {
                    matchedCount += 1;
                }
            }
            for (int p = 0; p < projectFields.size(); p++) {
                if (matchedUsers.get(i).getUserFields().stream().map(UserField::getField).collect(Collectors.toList()).contains(projectFields.get(p))) {
                    matchedCount += 1;
                }
            }

            for (int p = 0; p < projectTechnologies.size(); p++) {
                if (matchedUsers.get(i).getUserSkills().stream().map(UserSkill::getTechnology).collect(Collectors.toList()).contains(projectTechnologies.get(p))) {
                    matchedCount += 1;
                }
            }

            percentMatched = Double.parseDouble(decimalFormat.format((matchedCount / (totalProjectTraits * 1.0)) * 100));

            Major major = majorRepository.findByUser(matchedUsers.get(i).getId());

            recommendationData = UserRecommendationData.transform(matchedUsers.get(i),
                    userFieldRepository.findAllByUser(matchedUsers.get(i)), userSkillRepository.findAllByUser(matchedUsers.get(i)));
            recommendationData.setMajor(Objects.nonNull(major) ? major.getName() : null);
            recommendationData.setPercentMatched(percentMatched);
            matchedUserList.add(recommendationData);
        }
        return matchedUserList;
    }

}
