package com.fstart.service.common.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * EmailConstant
 *
 * @author: VuongVT2
 * @since: 2022/05/13
 */
@Getter
@Component
@PropertySource(value = "classpath:email-${spring.profiles.active}.properties", encoding = "utf-8")
public class EmailConstant {
    @Value("${email.host}")
    private String host;
    @Value("${email.port}")
    private int port;
    @Value("${email.sender}")
    private String sender;
    @Value("${email.password}")
    private String password;



    String SUBJECT_BLOCK_USER = "Your account has been suspended";
    String SUBJECT_UNBLOCK_USER = "Your account has been unlocked";
    String SUBJECT_BLOCK_PROJECT = "Your project has been suspended";
    String SUBJECT_UNBLOCK_PROJECT = "Your project has been unlocked";
    String SUBJECT_VERIFY_ACCOUNT = "Please verify your email address to complete the registration";
    String SUBJECT_REQUEST = "There's a new request to join your project";
    String SUBJECT_ACCEPT_REQUEST = "Your request has been accepted";
    String SUBJECT_INVITATION = "Somebody has sent you an invitation to join their project";
    String SUBJECT_ACCEPT_INVITATION = "Your invitation has been accepted";
    String EMAIL_ADMIN = "fstart.begin@gmail.com";
}
