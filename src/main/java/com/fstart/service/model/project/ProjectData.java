package com.fstart.service.model.project;

import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.entity.Project;
import com.fstart.service.entity.ProjectField;
import com.fstart.service.entity.ProjectTechnology;
import com.fstart.service.model.common.CommonData;
import com.fstart.service.model.discussion.DiscussionData;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProjectData
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectData implements Serializable {

    private String id;
    private String logo;
    private String title;
    private String subTitle;
    private String description;
    private String createdAt;
    private String updatedAt;
    private String privacy;
    private String status;
    private String reason;
    private int numberOfMember;
    private int numberOfCurrentMember;
    private boolean callForInvestment;
    private List<CommonData> fields;
    private List<CommonData> technologies;
    private List<DocumentData> documents;
    private String positionInProject;
    private List<ProjectPositionData> projectPositionList;
    private List<ProjectTeamMemberData> projectTeamMemberList;
    private List<DiscussionData> discussions;



    public static ProjectData transform(Project project, List<ProjectField> projectFields, List<ProjectTechnology> projectTechnologies, int numberOfMember, int numberOfCurrentMember) {
        List<CommonData> fieldDataList = projectFields.stream()
                .map(field -> DataBuilder.to(field.getField(), CommonData.class))
                .collect(Collectors.toList());
        List<CommonData> technologyDataList = projectTechnologies.stream()
                .map(technology -> DataBuilder.to(technology.getTechnology(), CommonData.class))
                .collect(Collectors.toList());

        ProjectData projectData = DataBuilder.to(project, ProjectData.class);
        projectData.setStatus(project.getStatus().name());
        projectData.setPrivacy(project.getPrivacy().name());
        projectData.setFields(fieldDataList);
        projectData.setTechnologies(technologyDataList);
        projectData.setNumberOfMember(numberOfMember);
        projectData.setNumberOfCurrentMember(numberOfCurrentMember);
        return projectData;
    }

}
