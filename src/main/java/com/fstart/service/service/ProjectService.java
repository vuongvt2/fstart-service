package com.fstart.service.service;

import com.fstart.service.enumeration.ERole;
import com.fstart.service.model.common.DataWrapper;
import com.fstart.service.model.common.PagedResponse;
import com.fstart.service.model.event.AvailableProjectEventData;
import com.fstart.service.model.home.HomeProjectData;
import com.fstart.service.model.project.*;

import java.util.List;

/**
 * ProjectService
 *
 * @author: VuongVT2
 * @since: 2022/01/16
 */
public interface ProjectService {


    ProjectData getProjectById(String id, String userId);

    DataWrapper createProject(String userId, ProjectUpsertForm projectForm);

    DataWrapper acceptProject(String roleId, String projectId);

    DataWrapper rejectProject(String roleId, String projectId, String reason);

    DataWrapper updateProject(String userId, ProjectFormUpdate projectFormUpdate);

    PagedResponse<HomeProjectData> filterProject(ERole role, String search, String status, String fieldId, String technologyId, int page, int size);

    List<HomeProjectData> getAllProjectByUser(String userId);

    List<ProjectTeamMemberData> getAllTeamMember(String userId, String projectId);

    List<ProjectPositionData> getAllRecruitment(String userId, String projectId);

    DataWrapper blockProject(String roleId, String projectId, String reason);

    DataWrapper unblockProject(String roleId, String projectId);

    String uploadLogo(String userId, ProjectLogoForm projectLogoForm);

    List<DocumentData> uploadDocuments(String userId, ProjectDocumentForm projectDocumentForm);

    boolean deleteDocument(String userId, String projectId, Long documentId);

    boolean removeMember(String userId, String projectId, String memberId);

    boolean leaveProject(String userId, String projectId);

    List<AvailableProjectPositionData> getAllProjectPositionByUser(String userId, String currentUserId);

    boolean deleteProject(String userId, String projectId);

    List<AvailableProjectEventData> getAllAvailableProjectEvent(String eventId, String userId);
}
