package com.fstart.service.model.invitation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * RequestForm
 *
 * @author: VuongVT2
 * @since: 2022/03/12
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestForm {

    @NotBlank
    @Length(max = 10)
    private String projectId;

    private String positionId;
}
