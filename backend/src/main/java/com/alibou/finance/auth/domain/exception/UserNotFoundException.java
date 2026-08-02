package com.alibou.finance.auth.domain.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
