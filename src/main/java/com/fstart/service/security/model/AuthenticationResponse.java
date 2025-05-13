package com.fstart.service.security.model;

import lombok.*;

import java.io.Serializable;

/**
 * AuthenticationResponse
 *
 * @author VuongVT2
 * @since 2021/10/23
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse implements Serializable {

    private String username;
    private String accessToken;
    private String role;
    private String expirationTime;

}
