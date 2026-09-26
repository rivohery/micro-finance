package com.alibou.finance.shared.domain.exception;

public class ObjectInvalidException extends RuntimeException{
    public ObjectInvalidException() {
    }

    public ObjectInvalidException(String message) {
        super(message);
    }
}
