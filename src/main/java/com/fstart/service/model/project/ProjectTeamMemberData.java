package com.fstart.service.model.project;

import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.entity.ProjectTeamMember;
import com.fstart.service.entity.User;
import com.fstart.service.model.common.CommonData;
import lombok.*;

import java.util.Objects;

/**
 * ProjectTeamMemberData
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamMemberData {

    private String userId;
    private String firstName;
    private String lastName;
    private String avatar;
    private String major;
    private CommonData position;

    public static ProjectTeamMemberData transform(ProjectTeamMember projectTeamMember) {
        User user = projectTeamMember.getUser();
        CommonData position = DataBuilder.to(projectTeamMember.getPosition(), CommonData.class);

        ProjectTeamMemberData data = ProjectTeamMemberData.builder()
                .position(position)
                .build();
        if (Objects.nonNull(user)) {
            data.setUserId(user.getId());
            data.setFirstName(user.getFirstName());
            data.setLastName(user.getLastName());
            data.setAvatar(user.getAvatar());
            data.setMajor(Objects.nonNull(user.getMajor()) ? user.getMajor().getName() : null);
        }

        return data;
    }
}
