package com.alibou.finance.account.infrastructure.adapter.out.port.service;

import com.alibou.finance.account.domain.out.service.TransfertConfirmationExecutor;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationInfo;
import com.alibou.finance.account.infrastructure.email.enums.EmailTemplateName;
import com.alibou.finance.account.infrastructure.email.exception.SendingEmailException;
import com.alibou.finance.account.infrastructure.email.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransfertConfirmationEmailAdapter implements TransfertConfirmationExecutor {

    private final EmailService emailService;
    @Override
    public void sendTransactionConfirmation(TransfertConfirmationInfo transfertConfirmationInfo) {
        try{
            final String templateName = EmailTemplateName.TRANSFERT_CONFIRMATION.getTemplate();
            final String subject = EmailTemplateName.TRANSFERT_CONFIRMATION.getSubject();

            Map<String, Object> variables = new HashMap<>();
            variables.put("client", transfertConfirmationInfo.username().value());
            variables.put("sourceAccountNumber", transfertConfirmationInfo.sourceAccountNumber());
            variables.put("targetAccountNumber", transfertConfirmationInfo.targetAccountNumber());
            variables.put("transfertCurrency", transfertConfirmationInfo.transfertCurrencyCode());
            variables.put("targetCurrency", transfertConfirmationInfo.targetCurrencyCode());
            variables.put("originalAmont", transfertConfirmationInfo.originalAmount());
            variables.put("exchangeRate", transfertConfirmationInfo.exchangeRate());
            variables.put("finalAmount", transfertConfirmationInfo.finalAmount());
            variables.put("dateTransfert", transfertConfirmationInfo.dateTransfert());

            String destinationEmail = transfertConfirmationInfo.userEmail().value();
            emailService.sendEmail(destinationEmail, templateName, subject, variables);
            log.info("Email confirmation sent successfully");
        } catch (MessagingException ex){
            ex.printStackTrace();
            log.info("Failed; no email confirmation has sent");
            throw new SendingEmailException(ex.getMessage());
        }
    }
}
