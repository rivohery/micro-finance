package com.alibou.finance.shared.domain.exception;

public class IllegalArgumentException extends RuntimeException{

    public IllegalArgumentException() {
    }

    public IllegalArgumentException(String message) {
        super(message);
    }
}
