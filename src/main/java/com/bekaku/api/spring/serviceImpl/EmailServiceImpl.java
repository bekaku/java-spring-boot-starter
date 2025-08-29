package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.service.EmailService;
import com.bekaku.api.spring.util.ConstantData;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Value("${app.mail-config.noreply-address}")
    String NOREPLY_ADDRESS;

    @Value("classpath:/static/img/gd5-logo.png")
    private Resource resourceFile;

    @Value("${app.mail-config.tokenExpire}")
    int tokenExpire;

    @Autowired
    private I18n i18n;
    Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            logger.info("Try sending");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (MailException exception) {
            logger.info("MailException sending");
            exception.printStackTrace();
        }
    }

    @Override
    public void sendSimpleMessageUsingTemplate(String to, String subject, String... templateModel) {
        String text = Arrays.toString(templateModel);
        sendSimpleMessage(to, subject, text);
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            // pass 'true' to the constructor to create a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(NOREPLY_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice.jpg", file);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageUsingThymeleafTemplate(String htmlTemplate, String to, String subject, Map<String, Object> templateModel) throws MessagingException {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process(htmlTemplate, thymeleafContext);
        sendHtmlMessage(to, subject, htmlBody);
    }

    @Override
    public void sendEmailRecoveryToken(AccessToken accessToken) throws MessagingException {
        Map<String, Object> templateModel = new HashMap<>();
        String toUser =  i18n.getMessage("app.user");
        templateModel.put("bodyText", i18n.getMessage("email.forgot.body", toUser));
        templateModel.put("tokenTitle", i18n.getMessage("email.forgot.token.title"));
        templateModel.put("expireText", i18n.getMessage("email.forgot.token.expire", tokenExpire));
        templateModel.put("regardsText", i18n.getMessage("regards"));
        templateModel.put("supportText", i18n.getMessage("team.support"));
        templateModel.put("token", accessToken.getToken());
        sendMessageUsingThymeleafTemplate(
                ConstantData.EMAIL_TEMPLATE_FORGOT,
                accessToken.getAppUser().getEmail(),
                i18n.getMessage("email.forgot.title"),
                templateModel);
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(NOREPLY_ADDRESS);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addInline("attachment.png", resourceFile);
        emailSender.send(message);
    }
}
