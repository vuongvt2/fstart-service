package com.fstart.service.security.service;

import javax.servlet.http.HttpServletRequest;

/**
 * AccessTokenService
 *
 * @author VuongVT2
 * @since 2021/10/22
 */
public interface AccessTokenService {

    String generateAccessToken(UserDetailsImpl userDetails);

    String generateAccessTokenFromUser(String id, String username, String role);

    String getUserID(final HttpServletRequest request);

    String getUserRole(final HttpServletRequest request);

    String getUserNameFromAccessToken(String accessToken);

    boolean validateAccessToken(String accessToken);

}
