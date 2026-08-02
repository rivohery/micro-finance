package com.alibou.finance.account.infrastructure.email.service;

import com.alibou.finance.account.domain.out.service.dto.TransfertConfirmationInfo;
import com.alibou.finance.account.infrastructure.email.enums.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final String SOURCE_ADDRESS_EMAIL = "contact@microfinance.com";

    @Async
    public void sendEmail(
            @NonNull String destinationEmail,
            @NonNull String templateName,
            @NonNull String subject,
            @NonNull Map<String, Object> variables
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                UTF_8.name()
        );
        messageHelper.setFrom(SOURCE_ADDRESS_EMAIL);

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(subject);

        String htmlTemplate = templateEngine.process(templateName, context);
        messageHelper.setText(htmlTemplate, true);
        messageHelper.setTo(destinationEmail);

        javaMailSender.send(mimeMessage);
        log.info(String.format("INFO - Email successfully sent to %s with template %s ", destinationEmail, templateName));

    }
}
