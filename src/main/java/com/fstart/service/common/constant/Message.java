package com.fstart.service.common.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Common Message
 *
 * @author VuongVT2
 * @since 2020/06/13
 */
@Getter
@Setter
@Component
@PropertySource(value = "classpath:message.properties", encoding = "utf-8")
public class Message {

    @Value("${ERROR.UNAUTHORIZED}")
    private String errorUnauthorized;

    @Value("${ERROR.BAD_REQUEST}")
    private String errorBadRequest;

    @Value("${ERROR.PROCEED_ERROR}")
    private String errorProceedError;

    @Value("${ERROR.SIGN_IN_ERROR}")
    private String errorSignInError;

    @Value("${ERROR.UPLOAD_FILE_ERROR}")
    private String errorUploadFileError;

    @Value("${WARN.USER_NOT_FOUND}")
    private String warnUserNotFound;

    @Value("${WARN.NO_DATA}")
    private String warnNoData;

    @Value("${WARN.OUT_OF_SLOT}")
    private String outOfSlot;

    @Value("${WARN.DUPLICATE_DATA}")
    private String duplicateData;

    @Value("${WARN.EXIST_CONSTRAINT_DATA}")
    private String existConstraintData;

    @Value("${ERROR.EXIST_EMAIL}")
    private String errorExistEmail;

    @Value("${WARN.REPORT_SPAM}")
    private String reportSpam;

}
