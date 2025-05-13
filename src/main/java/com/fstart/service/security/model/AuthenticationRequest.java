package com.fstart.service.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * AuthenticationRequest
 *
 * @author VuongVT2
 * @since 2021/10/22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements Serializable {

    @NotBlank
    private String username;
    @NotBlank
    private String password;

}
