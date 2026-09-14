package com.alibou.finance.account.infrastructure.email.enums;

import lombok.Getter;

public enum EmailTemplateName {
    TRANSFERT_CONFIRMATION("transfert-confirmation.html", "Confirmation du transfert");
    @Getter
    private final String template;
    @Getter
    private final String subject;

    EmailTemplateName(String template, String subject){
        this.template = template;
        this.subject = subject;
    }
}
