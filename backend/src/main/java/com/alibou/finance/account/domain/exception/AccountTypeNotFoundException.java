package com.alibou.finance.account.domain.exception;

public class AccountTypeNotFoundException extends RuntimeException{

    public AccountTypeNotFoundException() {
    }

    public AccountTypeNotFoundException(String message) {
        super(message);
    }
}
