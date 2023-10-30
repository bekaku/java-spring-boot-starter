package com.grandats.api.givedeefive.controller.test;


import com.grandats.api.givedeefive.controller.api.BaseApiController;
import com.grandats.api.givedeefive.properties.AppProperties;
import com.grandats.api.givedeefive.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class EmailController extends BaseApiController {

    Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;
    private final AppProperties appProperties;

    private static final Map<String, Map<String, String>> labels;

    static {
        labels = new HashMap<>();

        //Simple email
        Map<String, String> props = new HashMap<>();
        props.put("headerText", "Send Simple Email");
        props.put("messageLabel", "Message");
        props.put("additionalInfo", "");
        labels.put("send", props);

        //Email with template
        props = new HashMap<>();
        props.put("headerText", "Send Email Using Text Template");
        props.put("messageLabel", "Template Parameter");
        props.put("additionalInfo",
                "The parameter value will be added to the following message template:<br>" +
                        "<b>This is the test email template for your email:<br>'Template Parameter'</b>"
        );
        labels.put("sendTemplate", props);

        //Email with attachment
        props = new HashMap<>();
        props.put("headerText", "Send Email With Attachment");
        props.put("messageLabel", "Message");
        props.put("additionalInfo", "To make sure that you send an attachment with this email, change the value for the 'attachment.invoice' in the application.properties file to the path to the attachment.");
        labels.put("sendAttachment", props);

    }

    @GetMapping("/test/sendMailSimple")
    public ResponseEntity<Object> sendMailSimple() {
        logger.info("Start sending");
        emailService.sendSimpleMessage("baekaku@gmail.com",
                "sendSimple from spring boot", "Just test message from mail.");
        return this.responseEntity(HttpStatus.OK);
    }
    @GetMapping("/test/sendMailHtml")
    public ResponseEntity<Object> sendMailHtml() throws  IOException {
        logger.info("Start sending");
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("recipientName", "baekaku");
        templateModel.put("text", "Test Send HtML template");
        templateModel.put("senderName", "supersynapse");
        emailService.sendMessageUsingThymeleafTemplate(
                "baekaku@gmail.com",
                "Send from html template",
                templateModel);
        return this.responseEntity(HttpStatus.OK);
    }
    @GetMapping("/test/sendMailAttachment")
    public ResponseEntity<Object> sendMailAttachment() {
        logger.info("Start sending");
        emailService.sendMessageWithAttachment(
                "baekaku@gmail.com",
                "Send with attachment",
                "Mail Detail",
                appProperties.getUploadPath()+"/images/202204/1_1651129562336_7221fbfdc6fd45cd8300f87c6b32f177.jpg"
        );
        return this.responseEntity(HttpStatus.OK);
    }
}
