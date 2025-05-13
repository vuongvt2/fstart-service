package com.fstart.service.model.project;

import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.entity.Project;
import com.fstart.service.entity.ProjectField;
import com.fstart.service.entity.ProjectTechnology;
import com.fstart.service.model.common.CommonData;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProjectRecommendationData
 *
 * @author: VuongVT2
 * @since: 2022/04/15
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRecommendationData {

    private String id;
    private String title;
    private String logo;
    private int numberOfMember;
    private int numberOfCurrentMember;
    private List<CommonData> fields;
    private List<CommonData> technologies;
    private double percentMatched;

    public static ProjectRecommendationData transform(Project project, List<ProjectField> projectFields, List<ProjectTechnology> projectTechnologies) {
        ProjectRecommendationData result = DataBuilder.to(project, ProjectRecommendationData.class);

        List<CommonData> fields = projectFields.stream()
                .map(projectField -> DataBuilder.to(projectField.getField(), CommonData.class))
                .collect(Collectors.toList());
        result.setFields(fields);

        List<CommonData> technologies = projectTechnologies.stream()
                .map(projectField -> DataBuilder.to(projectField.getTechnology(), CommonData.class))
                .collect(Collectors.toList());
        result.setTechnologies(technologies);

        return result;
    }

}