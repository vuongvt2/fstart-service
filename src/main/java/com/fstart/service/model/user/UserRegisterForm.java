package com.fstart.service.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * UserRegisterForm
 *
 * @author VuongVT2
 * @since 2022/04/13
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterForm implements Serializable {

    @NotBlank
    @Length(max = 320)
    private String email;

    @NotBlank
    @Length(max = 150)
    private String firstName;

    @NotBlank
    @Length(max = 150)
    private String lastName;

    @NotBlank
    private String password;

}
