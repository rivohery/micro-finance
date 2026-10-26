package com.alibou.finance.shared.domain.exception;

public class DuplicateValueException extends RuntimeException{
    public DuplicateValueException() {
    }

    public DuplicateValueException(String message) {
        super(message);
    }
}
