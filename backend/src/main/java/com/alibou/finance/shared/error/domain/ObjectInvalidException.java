package com.alibou.finance.shared.error.domain;

public class ObjectInvalidException extends RuntimeException{
    public ObjectInvalidException() {
    }

    public ObjectInvalidException(String message) {
        super(message);
    }
}
