package com.alibou.finance.shared.domain.exception;

public class FileUploadedException extends RuntimeException{
    public FileUploadedException() {
    }

    public FileUploadedException(String message) {
        super(message);
    }
}
