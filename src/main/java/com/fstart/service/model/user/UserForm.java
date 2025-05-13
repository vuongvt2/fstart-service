package com.fstart.service.model.user;

import com.fstart.service.model.common.ExperienceForm;
import com.fstart.service.model.common.UserPositionForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * UserForm
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForm implements Serializable {

    @NotBlank
    @Length(max = 150)
    private String firstName;

    @NotBlank
    @Length(max = 150)
    private String lastName;

    @Length(max = 255)
    private String bio;

    @Length(max = 12)
    private String phoneNumber;

    @Length(max = 150)
    private String address;

    private String majorId;

    @Length(max = 150)
    private String facebookLink;

    @Length(max = 150)
    private String githubLink;

    @Length(max = 150)
    private String linkedinLink;

    private List<ExperienceForm> experiences;
    private List<String> positionIds;
    private List<String> fieldIds;
    private List<String> techIds;
}
