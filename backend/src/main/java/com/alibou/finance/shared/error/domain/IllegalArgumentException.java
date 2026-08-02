package com.alibou.finance.shared.error.domain;

public class IllegalArgumentException extends RuntimeException{

    public IllegalArgumentException() {
    }

    public IllegalArgumentException(String message) {
        super(message);
    }
}
