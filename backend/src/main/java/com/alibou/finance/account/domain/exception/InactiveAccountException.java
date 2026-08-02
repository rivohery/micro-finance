package com.alibou.finance.account.domain.exception;

public class InactiveAccountException extends RuntimeException{
    public InactiveAccountException() {
    }

    public InactiveAccountException(String message) {
        super(message);
    }
}
