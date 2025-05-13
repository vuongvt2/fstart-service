package com.fstart.service.model.user;

import com.fstart.service.common.utils.DataBuilder;
import com.fstart.service.entity.User;
import com.fstart.service.entity.UserField;
import com.fstart.service.entity.UserSkill;
import com.fstart.service.model.common.CommonData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserRecommendationData
 *
 * @author: VuongVT2
 * @since: 2022/04/28
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendationData {

    private String id;
    private String firstName;
    private String lastName;
    private String avatar;
    private String major;
    private List<CommonData> userFields;
    private List<CommonData> positions;
    private double percentMatched;

    public static UserRecommendationData transform(final User user, final List<UserField> userFields, final List<UserSkill> userSkills) {


        List<CommonData> positionsDataList = user.getUserPositions()
                .stream()
                .map(userPosition -> new CommonData(userPosition.getPosition().getId(), userPosition.getPosition().getName())).collect(Collectors.toList());

        List<CommonData> fields = userFields.stream()
                .map(userField -> DataBuilder.to(userField.getField(), CommonData.class))
                .collect(Collectors.toList());

        UserRecommendationData userRecommendationData = DataBuilder.to(user, UserRecommendationData.class);
        userRecommendationData.setPositions(positionsDataList);
        userRecommendationData.setUserFields(fields);

        return userRecommendationData;
    }
}