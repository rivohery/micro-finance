package com.alibou.finance.account.infrastructure.adapter.out.service;

import com.alibou.finance.account.domain.out.service.TransfertConfirmationPort;
import com.alibou.finance.account.domain.out.service.dto.TransfertConfirmationInfo;
import com.alibou.finance.account.infrastructure.email.enums.EmailTemplateName;
import com.alibou.finance.account.infrastructure.email.exception.SendingEmailException;
import com.alibou.finance.account.infrastructure.email.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransfertConfirmationEmailAdapter implements TransfertConfirmationPort {

    private final EmailService emailService;
    @Override
    public int sendTransactionConfirmation(TransfertConfirmationInfo transfertConfirmationInfo) {
        try{
            final String templateName = EmailTemplateName.TRANSFERT_CONFIRMATION.getTemplate();
            final String subject = EmailTemplateName.TRANSFERT_CONFIRMATION.getSubject();

            Map<String, Object> variables = new HashMap<>();
            variables.put("client", transfertConfirmationInfo.user().getUsername().value());
            variables.put("sourceAccountNumber", transfertConfirmationInfo.sourceAccountNumber());
            variables.put("targetAccountNumber", transfertConfirmationInfo.targetAccountNumber());
            variables.put("transfertCurrency", transfertConfirmationInfo.transfertCurrencyCode());
            variables.put("targetCurrency", transfertConfirmationInfo.targetCurrencyCode());
            variables.put("originalAmont", transfertConfirmationInfo.originalAmount());
            variables.put("exchangeRate", transfertConfirmationInfo.exchangeRate());
            variables.put("finalAmount", transfertConfirmationInfo.finalAmount());
            variables.put("dateTransfert", transfertConfirmationInfo.dateTransfert());

            String destinationEmail = transfertConfirmationInfo.user().getEmail().value();
            emailService.sendEmail(destinationEmail, templateName, subject, variables);
            return 1;
        } catch (MessagingException ex){
            ex.printStackTrace();
            throw new SendingEmailException(ex.getMessage());
        }
    }
}
