package com.alibou.finance.account.domain.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String message) {
        super(message);
    }
}
