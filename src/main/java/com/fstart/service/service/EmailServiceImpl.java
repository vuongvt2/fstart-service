package com.fstart.service.service;

import com.fstart.service.model.email.EmailMessageConfig;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * EmailServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/05/13
 */
@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Override
    @Async
    public void sendEmail(List<EmailMessageConfig> configs) throws MessagingException {
        List<MimeMessage> messages = buildEmailMessage(thymeleafTemplateEngine, javaMailSender, configs);
        for (MimeMessage message : messages) {
            javaMailSender.send(message);
        }
    }
    public static List<MimeMessage> buildEmailMessage(SpringTemplateEngine thymeleafTemplateEngine, JavaMailSender javaMailSender, List<EmailMessageConfig> configs) throws MessagingException {
        List<MimeMessage> result = new ArrayList<>();
        for (EmailMessageConfig config : configs) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(config.getTo());
            helper.setSubject(config.getSubject());
            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(config.getTemplateModel());
            String htmlBody = thymeleafTemplateEngine.process(config.getTemplate(), thymeleafContext);
            helper.setText(htmlBody, true);
            result.add(message);
        }
        return result;
    }
}
