package com.fstart.service.controller;

import com.fstart.service.common.constant.AppConstant;
import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.event.AvailableProjectEventData;
import com.fstart.service.model.home.HomeProjectData;
import com.fstart.service.model.project.*;
import com.fstart.service.security.service.AccessTokenService;
import com.fstart.service.service.ProjectService;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ProjectController
 *
 * @author: VuongVT2
 * @since: 2022/01/16
 */
@RestController
@RequestMapping(value = "/fs/api/v1/project")
public class ProjectController {

    private final AccessTokenService accessTokenService;
    private final ProjectService projectService;

    public ProjectController(final AccessTokenService accessTokenService,
                             final ProjectService projectService) {
        this.accessTokenService = accessTokenService;
        this.projectService = projectService;
    }

    @PostMapping(value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public DataWrapper createProject(HttpServletRequest request, @Valid ProjectUpsertForm projectForm) {
        String userId = accessTokenService.getUserID(request);
        return projectService.createProject(userId, projectForm);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper updateProject(HttpServletRequest request,
                                     @RequestBody @Valid ProjectFormUpdate projectFormUpdate) {
        String userId = accessTokenService.getUserID(request);
        return projectService.updateProject(userId, projectFormUpdate);
    }

    @GetMapping(value = "/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper acceptProject(@RequestParam(value = "projectId") @NotBlank String projectId,
                                     HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        return projectService.acceptProject(roleId, projectId);
    }

    @GetMapping(value = "/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper rejectProject(@RequestParam(value = "projectId") @NotBlank String projectId,
                                     @RequestParam(value = "reason") @NotBlank String reason,
                                     HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        return projectService.rejectProject(roleId, projectId, reason);
    }

    @GetMapping(value = "/public/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectData getProject(@PathVariable(name = "id") @NotBlank String id, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.getProjectById(id, userId);
    }

    @GetMapping(value = "/public/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<HomeProjectData> filterProject(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                        @RequestParam(name = "fieldId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String fieldId,
                                                        @RequestParam(name = "technologyId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String technologyId,
                                                        @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                        @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        return projectService.filterProject(null, search, null, fieldId, technologyId, page, size);
    }

    @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<HomeProjectData> filterProjectForAdmin(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                                @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
                                                                @RequestParam(name = "fieldId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String fieldId,
                                                                @RequestParam(name = "technologyId", defaultValue = AppConstant.DEFAULT_STR_VALUE) String technologyId,
                                                                @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                                @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                                HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        ERole role = StringUtils.hasLength(roleId) ? ERole.valueOf(roleId) : null;
        return projectService.filterProject(role, search, status, fieldId, technologyId, page, size);
    }

    @GetMapping(value = "/public/all-team-member", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectTeamMemberData> getAllTeamMember(@RequestParam(value = "projectId") @NotBlank String projectId,
                                                        HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.getAllTeamMember(userId, projectId);
    }

    @GetMapping(value = "/all-recruitment", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectPositionData> getAllRecruitment(@RequestParam(value = "projectId") @NotBlank String projectId,
                                                       HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.getAllRecruitment(userId, projectId);
    }

    @GetMapping(value = "/my-projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HomeProjectData> getAllProjectByUser(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.getAllProjectByUser(userId);
    }

    @GetMapping(value = "/block", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper blockProject(@RequestParam(value = "projectId") @NotBlank String projectId,
                                    @RequestParam(value = "reason") @NotBlank String reason,
                                    HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        return projectService.blockProject(roleId, projectId, reason);
    }

    @GetMapping(value = "/unblock", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataWrapper unblockProject(@RequestParam(value = "projectId") @NotBlank String projectId,
                                      HttpServletRequest request) {
        String roleId = accessTokenService.getUserRole(request);
        return projectService.unblockProject(roleId, projectId);
    }

    @PostMapping(value = "/upload-logo", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadLogo(@Valid ProjectLogoForm projectLogoForm,
                             HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.uploadLogo(userId, projectLogoForm);
    }

    @PostMapping(value = "/upload-document", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<DocumentData> uploadDocuments(@Valid ProjectDocumentForm projectDocumentForm,
                                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.uploadDocuments(userId, projectDocumentForm);
    }

    @GetMapping(value = "/delete-document", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteDocument(@RequestParam(value = "projectId") @NotBlank String projectId,
                                  @RequestParam(value = "documentId") @NotNull Long documentId,
                                  HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.deleteDocument(userId, projectId, documentId);
    }

    @GetMapping(value = "/remove-member", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeMember(@RequestParam(value = "projectId") @NotBlank String projectId,
                                @RequestParam(value = "memberId") @NotBlank String memberId,
                                HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.removeMember(userId, projectId, memberId);
    }

    @GetMapping(value = "/leave-project", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean leaveProject(@RequestParam(value = "projectId") @NotBlank String projectId,
                                HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.leaveProject(userId, projectId);
    }


    @GetMapping(value = "/all-project-position", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableProjectPositionData> getAllProjectPositionByUser(@RequestParam(value = "userId") @NotBlank String userId,
                                                                          HttpServletRequest request) {
        String currentUserId = accessTokenService.getUserID(request);
        return projectService.getAllProjectPositionByUser(userId, currentUserId);
    }

    @GetMapping(value = "/delete-project", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteProject(@RequestParam(value = "projectId") @NotBlank String projectId,
                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.deleteProject(userId, projectId);
    }

    @GetMapping(value = "/available-project-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableProjectEventData> getAllAvailableProjectEvent(@RequestParam(value = "eventId") @NotBlank String eventId,
                                                                       HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return projectService.getAllAvailableProjectEvent(eventId, userId);
    }
}
