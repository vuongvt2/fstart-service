package com.fstart.service.model.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * ProjectFormUpdate
 *
 * @author: VuongVT2
 * @since: 2022/05/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFormUpdate {

    @NotBlank
    @Length(max = 10)
    private String id;

    @NotBlank
    @Length(max = 150)
    private String title;

    @Length(max = 255)
    private String subTitle;

    @NotBlank
    private String description;

    @NotBlank
    @Length(max = 20)
    private String privacy;

    private boolean callForInvestment;

    private List<String> fields;

    private List<String> technologies;

    private List<ProjectPositionForm> projectPositions;

    private List<ProjectTeamMemberForm> projectTeamMembers;

}
