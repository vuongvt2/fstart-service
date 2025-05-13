package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.ExperienceData;
import com.fstart.service.model.common.ExperienceForm;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.home.HomeUserData;
import com.fstart.service.model.user.UserForm;
import com.fstart.service.model.user.UserOverviewData;
import com.fstart.service.model.user.UserRegisterForm;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * UserController
 *
 * @author: VuongVT2
 * @since: 2022/01/11
 */
@Validated
@RestController
@RequestMapping(value = "/fs/api/v1/user")
public class UserController {

    private final AccessTokenService accessTokenService;
    private final UserService userService;

    public UserController(final AccessTokenService accessTokenService,
                          final UserService userService) {
        this.accessTokenService = accessTokenService;
        this.userService = userService;
    }

    @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<HomeUserData> filterUserForAdmin(
            @RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
            @RequestParam(name = "positionId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String positionId,
            @RequestParam(name = "skillId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String skillId,
            @RequestParam(name = "fieldId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String fieldId,
            @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
            @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
            HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        ERole role = StringUtils.hasLength(roleId) ? ERole.valueOf(roleId) : null;
        return userService.filterUser(role, search, positionId, fieldId, skillId, status, page, size);
    }

    @GetMapping(value = "/public/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<HomeUserData> filterUser(
            @RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
            @RequestParam(name = "positionId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String positionId,
            @RequestParam(name = "skillId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String skillId,
            @RequestParam(name = "fieldId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String fieldId,
            @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        return userService.filterUser(null, search, positionId, fieldId, skillId, null, page, size);
    }

    @GetMapping(value = "/user-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserOverviewData getUser(@RequestParam("id") String id,
                                    HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return userService.getUser(role, id);
    }

    @GetMapping(value = "/user-username", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserOverviewData getUserByUsername(@RequestParam("username") String username,
                                              HttpServletRequest request) {
        String role = accessTokenService.getUserRole(request);
        return userService.getUserByUsername(role, username);
    }

    @PostMapping(value = "/update",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@Valid @RequestBody UserForm userForm,
                           HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        userService.updateUser(userId, userForm);
    }

    @GetMapping(value = "/update-status",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUserStatus(@RequestParam("status") @NotBlank String status, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        userService.updateUserStatus(userId, status);
    }

    @PostMapping(value = "/update-avatar", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateAvatar(@RequestParam(value = "avatar") MultipartFile avatar,
                               HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return userService.updateAvatar(avatar, userId);
    }

    @PostMapping(value = "/public/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean register(@Valid @RequestBody @NotNull UserRegisterForm userRegisterForm) {
        return userService.register(userRegisterForm);
    }

    @GetMapping(value = "/public/verify-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public String verifyEmail(@RequestParam @NotBlank String userId,
                              @RequestParam @NotBlank String hash) {
        return userService.verifyEmail(userId, hash);
    }

    @GetMapping(value = "/public/send-verify-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean sendVerifyCode(@RequestParam @NotBlank String email) {
        return userService.sendVerifyCode(email);
    }

    @GetMapping(value = "/create-skill-set", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean createSkillSet(@RequestParam(value = "positionsId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String positionsId,
                                  @RequestParam(value = "fieldsId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String fieldsId,
                                  @RequestParam(value = "technologiesId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String technologiesId,
                                  HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return userService.createSkillSet(positionsId, fieldsId, technologiesId, userId);
    }

    @GetMapping(value = "/block", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper blockUser(@RequestParam(value = "userId") @NotBlank String userId,
                                 @RequestParam(value = "reason") @NotBlank String reason,
                                 HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        return userService.blockUser(roleId, userId, reason);
    }

    @GetMapping(value = "/unblock", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper unblockUser(@RequestParam(value = "userId") @NotBlank String userId,
                                   HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        return userService.unblockUser(roleId, userId);
    }

    @GetMapping(value = "/delete-experience", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteExperience(@RequestParam(value = "experienceId") @NotNull Long experienceId,
                                    HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return userService.deleteExperience(experienceId, userId);
    }

    @GetMapping(value = "/all-experiences", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExperienceData> getAllExperiencesByUserId(@RequestParam(value = "userId") @NotBlank String userId) {
        return userService.getAllExperiencesByUserId(userId);
    }

    @PostMapping(value = "/create-experience", produces = MediaType.APPLICATION_JSON_VALUE)
    public void createExperience(@RequestBody ExperienceForm experienceForm,
                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        userService.createExperience(experienceForm, userId);
    }

    @PostMapping(value = "/update-experience", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateExperience(@RequestBody ExperienceForm experienceForm,
                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        userService.updateExperience(experienceForm, userId);
    }
}