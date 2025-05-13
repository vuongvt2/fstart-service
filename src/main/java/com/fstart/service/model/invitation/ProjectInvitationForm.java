package com.fstart.service.model.invitation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * GroupInvitationForm
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInvitationForm implements Serializable  {

    @NotBlank
    @Length(max = 10)
    private String projectId;

    @NotEmpty
    private String receiverId;

    @NotBlank
    private String positionId;
}
