package com.bekaku.api.spring.service;

import com.bekaku.api.spring.model.AccessToken;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.Map;

public interface EmailService {
    void sendSimpleMessage(String to,
                           String subject,
                           String text);

    void sendSimpleMessageUsingTemplate(String to,
                                        String subject,
                                        String... templateModel);

    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);

    void sendMessageUsingThymeleafTemplate(String htmlTemplate, String to, String subject, Map<String, Object> templateModel) throws MessagingException;

    void sendEmailRecoveryToken(AccessToken accessToken) throws MessagingException;
}
