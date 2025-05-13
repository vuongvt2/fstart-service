package com.fstart.service.security.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * AuthUtils
 *
 * @author VuongVT2
 * @since 2022/01/11
 */
public class AuthUtils {

    public static String extractTokenFrom(HttpServletRequest request) {
        final String AUTH_TYPE = "Bearer";
        String authorizationHeader = request.getHeader("Authorization");

        if (StringUtils.hasLength(authorizationHeader) && authorizationHeader.startsWith(AUTH_TYPE)) {
            return authorizationHeader.substring(AUTH_TYPE.length() + 1, authorizationHeader.length());
        }

        return null;
    }

}
