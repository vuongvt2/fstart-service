package com.fstart.service.service;

import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.ExperienceData;
import com.fstart.service.model.common.ExperienceForm;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.home.HomeUserData;
import com.fstart.service.model.user.UserForm;
import com.fstart.service.model.user.UserOverviewData;
import com.fstart.service.model.user.UserRegisterForm;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * UserService
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
public interface UserService {

    UserOverviewData getUser(String role, String id);

    UserOverviewData getUserByUsername(String role, String username);

    void updateUser(String userId, UserForm userForm);

    void updateUserStatus(String userId, String status);

    String updateAvatar(MultipartFile avatar, String userId);

    PagedResponse<HomeUserData> filterUser(ERole role, String search, String positionId, String fieldId, String skillId, String status, Integer page, Integer size);

    boolean register(UserRegisterForm userRegisterForm);

    String verifyEmail(String userId, String hash);

    boolean sendVerifyCode(String email);

    boolean createSkillSet(String positionsId, String fieldsId, String technologiesId, String userId);

    DataWrapper blockUser(String roleId, String userId, String reason);

    DataWrapper unblockUser(String roleId, String userId);

    boolean deleteExperience(Long experienceId, String userId);

    List<ExperienceData> getAllExperiencesByUserId(String userId);

    void createExperience(ExperienceForm experienceForm, String userId);

    void updateExperience(ExperienceForm experienceForm, String userId);
}
