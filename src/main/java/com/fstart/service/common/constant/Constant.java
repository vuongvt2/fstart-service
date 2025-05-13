package com.fstart.service.common.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Constant
 *
 * @author VuongVT2
 * @since 2021/11/04
 */
@Getter
@Component
@PropertySource(value = "classpath:constant-${spring.profiles.active}.properties", encoding = "utf-8")
public class Constant {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.host}")
    private String appHost;

    @Value("${app.redirect_uri}")
    private String appRedirectURI;

    @Value("${app.redirect_uri.email}")
    private String appRedirectURIEmail;

    @Value("${app.redirect_uri.support}")
    private String appRedirectURISupport;

    @Value("${user.verification.expire}")
    private long userVerificationExpire;

    @Value("${app.redirect_uri.verify_account}")
    private String appRedirectURIVerifyAccount;

    @Value("${app.redirect_uri.google}")
    private String appRedirectURIGoogle;


}
