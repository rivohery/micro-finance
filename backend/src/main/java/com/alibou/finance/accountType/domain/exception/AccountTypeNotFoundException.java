package com.alibou.finance.accountType.domain.exception;

public class AccountTypeNotFoundException extends RuntimeException{

    public AccountTypeNotFoundException() {
    }

    public AccountTypeNotFoundException(String message) {
        super(message);
    }
}
