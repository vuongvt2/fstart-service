package com.fstart.service.service;

import com.fstart.service.model.common.CommonData;
import com.fstart.service.model.common.HomeData;
import com.fstart.service.model.common.MasterData;
import com.fstart.service.model.common.StatisticalData;
import com.fstart.service.model.project.ProjectRecommendationData;
import com.fstart.service.model.user.UserRecommendationData;

import java.util.List;

/**
 * MasterService
 *
 * @author VuongVT2
 * @since 2022/04/11
 */
public interface MasterService {

    HomeData getHomeData();

    List<CommonData> getAllViolations();

    List<ProjectRecommendationData> getRecommendationForUser(String userId);

    MasterData getAllFieldsAndTechnologiesAndPositions();

    List<UserRecommendationData> getRecommendationForProject(String projectId, String userId);

    StatisticalData getStatistical(String role);

    List<CommonData> getAllCountries();

}
