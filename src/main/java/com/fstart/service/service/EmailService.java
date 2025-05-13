package com.fstart.service.service;

import com.fstart.service.model.email.EmailMessageConfig;

import javax.mail.MessagingException;
import java.util.List;

/**
 * EmailService
 *
 * @author: VuongVT2
 * @since: 2022/05/13
 */
public interface EmailService {
    void sendEmail(List<EmailMessageConfig> configs) throws MessagingException;
}
