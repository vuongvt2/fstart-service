package com.fstart.service.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * SocialNetworkForm
 *
 * @author: VuongVT2
 * @since: 2022/03/29
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPositionForm {
    private Long id;

    @NotBlank
    @Length(max = 20)
    private String positionId;

    private boolean delete;
}
