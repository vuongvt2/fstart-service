package com.fstart.service.model.user;

import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.entity.*;
import com.fstart.service.model.common.CommonData;
import com.fstart.service.model.common.ExperienceData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * UserData
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOverviewData implements Serializable {

    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bio;
    private String avatar;
    private String email;
    private String status;
    private String role;
    private String address;
    private String facebookLink;
    private String githubLink;
    private String linkedinLink;
    private CommonData major;
    private List<CommonData> userFields;
    private List<CommonData> userSkills;
    private List<ExperienceData> experiences;
    private List<CommonData> positions;

    public static UserOverviewData transform(final User user, final List<Experience> experiences, final List<UserField> userFields, final List<UserSkill> userSkills) {
        List<ExperienceData> experienceDataList = experiences.stream()
                .map(experience -> ExperienceData.transform(experience))
                .collect(Collectors.toList());

        CommonData majorData = null;
        if (Objects.nonNull(user.getMajor())) {
            majorData = new CommonData(user.getMajor().getId(), user.getMajor().getName());
        }
        List<CommonData> positionsDataList = user.getUserPositions()
                .stream()
                .map(userPosition -> new CommonData(userPosition.getPosition().getId(), userPosition.getPosition().getName())).collect(Collectors.toList());

        List<CommonData> userFieldList = userFields
                .stream()
                .map(userField -> new CommonData(userField.getField().getId(), userField.getField().getName())).collect(Collectors.toList());

        List<CommonData> userSkillList = userSkills
                .stream()
                .map(userSkill -> new CommonData(userSkill.getTechnology().getId(), userSkill.getTechnology().getName())).collect(Collectors.toList());

        UserOverviewData userOverviewData = DataBuilder.to(user, UserOverviewData.class);
        userOverviewData.setMajor(majorData);
        userOverviewData.setRole(user.getRole().getId().toString());
        userOverviewData.setExperiences(experienceDataList);
        userOverviewData.setPositions(positionsDataList);
        userOverviewData.setUserFields(userFieldList);
        userOverviewData.setUserSkills(userSkillList);
        userOverviewData.setStatus(user.getStatus().name());

        return userOverviewData;
    }

}
