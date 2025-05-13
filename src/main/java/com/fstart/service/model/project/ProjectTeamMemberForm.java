package com.fstart.service.model.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProjectTeamMemberForm
 *
 * @author: VuongVT2
 * @since: 2022/05/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamMemberForm {
    private String positionId;
    private String userId;
}
