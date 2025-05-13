package com.fstart.service.controller;

import com.fstart.service.model.common.CommonData;
import com.fstart.service.model.common.HomeData;
import com.fstart.service.model.common.MasterData;
import com.fstart.service.model.common.StatisticalData;
import com.fstart.service.model.project.ProjectRecommendationData;
import com.fstart.service.model.user.UserRecommendationData;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.MasterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * MasterController
 *
 * @author VuongVT2
 * @since 2022/03/12
 */
@RestController
@RequestMapping(value = "/fs/api/v1/master")
public class MasterController {

    private final AccessTokenService accessTokenService;
    private final MasterService masterService;

    public MasterController(final AccessTokenService accessTokenService,
                            final MasterService masterService) {
        this.accessTokenService = accessTokenService;
        this.masterService = masterService;
    }

    @GetMapping(value = "/no-auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean noAuth(HttpServletRequest request) {
        return Objects.isNull(accessTokenService.getUserID(request));
    }

    @GetMapping(value = "/public/field-tech-position/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public MasterData getAllFieldsAndTechnologiesAndPositions() {
        return masterService.getAllFieldsAndTechnologiesAndPositions();
    }

    @GetMapping(value = "/public/home", produces = MediaType.APPLICATION_JSON_VALUE)
    public HomeData getHomeData() {
        return masterService.getHomeData();
    }

    @GetMapping(value = "/public/violation", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommonData> getViolations() {
        return masterService.getAllViolations();
    }

    @GetMapping(value = "/user-recommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectRecommendationData> getRecommendationForUser(HttpServletRequest request) {

        String userId = accessTokenService.getUserID(request);
        return masterService.getRecommendationForUser(userId);
    }

    @GetMapping(value = "/project-recommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserRecommendationData> getRecommendationForProject(@RequestParam(name = "projectId") String projectId,
                                                                    HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return masterService.getRecommendationForProject(projectId, userId);
    }

    @GetMapping(value = "/statistical", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatisticalData getStatistical(HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return masterService.getStatistical(role);
    }

    @GetMapping(value = "/countries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommonData> getCountries() {
        return masterService.getAllCountries();
    }

}
