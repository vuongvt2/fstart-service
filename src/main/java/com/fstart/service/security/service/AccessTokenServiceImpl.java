package com.fstart.service.security.service;

import com.fstart.service.common.constant.Constant;
import com.fstart.service.common.constant.Message;
import com.fstart.service.common.logging.AppLogger;
import com.fstart.service.security.utils.AuthUtils;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AccessTokenServiceImpl
 *
 * @author VuongVT2
 * @since 2021/10/22
 */
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final String USER_ID_KEY = "id";
    private static final String ROLE_KEY = "role";

    private final Message message;
    private final Constant constant;

    public AccessTokenServiceImpl(final Message message, final Constant constant) {
        this.message = message;
        this.constant = constant;
    }

    @Override
    public String generateAccessToken(final UserDetailsImpl userDetails) {
        String role = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
            .collect(Collectors.toList()).get(0);
        return generateAccessTokenFromUser(userDetails.getId(), userDetails.getUsername(), role);
    }

    @Override
    public String generateAccessTokenFromUser(final String id, final String username, final String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim(USER_ID_KEY, id)
                .claim(ROLE_KEY, role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + (constant.getJwtExpiration() * 60 * 1000)))
                .signWith(SignatureAlgorithm.HS512, constant.getJwtSecret())
                .compact();
    }

    @Override
    public String getUserNameFromAccessToken(String accessToken) {
        return Jwts.parser()
                .setSigningKey(constant.getJwtSecret())
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }

    @Override
    public String getUserID(final HttpServletRequest request) {
        String accessToken = AuthUtils.extractTokenFrom(request);
        return getUserFrom(accessToken, USER_ID_KEY);
    }

    @Override
    public String getUserRole(final HttpServletRequest request) {
        String accessToken = AuthUtils.extractTokenFrom(request);
        return getUserFrom(accessToken, ROLE_KEY);
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parser().setSigningKey(constant.getJwtSecret()).parseClaimsJws(accessToken);
            return true;
        } catch (SignatureException e) {
            AppLogger.errorLog("Invalid JWT signature: " + e.getMessage(), e);
        } catch (MalformedJwtException e) {
            AppLogger.errorLog("Invalid JWT token: " + e.getMessage(), e);
        } catch (ExpiredJwtException e) {
            AppLogger.errorLog("JWT token is expired: " + e.getMessage(), e);
        } catch (UnsupportedJwtException e) {
            AppLogger.errorLog("JWT token is unsupported: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            AppLogger.errorLog("JWT claims string is empty: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Private function
     */
    private String getUserFrom(final String accessToken, final String key) {
        try {
            return Objects.nonNull(accessToken) ? Jwts.parser().setSigningKey(constant.getJwtSecret())
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get(key)
                    .toString() : null;
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException(message.getErrorUnauthorized());
        }
    }

}
