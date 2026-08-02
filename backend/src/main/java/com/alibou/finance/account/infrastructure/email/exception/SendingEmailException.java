package com.alibou.finance.account.infrastructure.email.exception;

public class SendingEmailException extends RuntimeException{
    public SendingEmailException(String message) {
        super(message);
    }
}
