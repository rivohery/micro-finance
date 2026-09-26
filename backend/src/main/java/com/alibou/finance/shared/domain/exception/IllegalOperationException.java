package com.alibou.finance.shared.domain.exception;

public class IllegalOperationException extends RuntimeException{
    public IllegalOperationException() {
    }

    public IllegalOperationException(String message) {
        super(message);
    }
}
