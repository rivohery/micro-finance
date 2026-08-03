package com.alibou.finance.shared.domain;

public class FileUploadedException extends RuntimeException{
    public FileUploadedException() {
    }

    public FileUploadedException(String message) {
        super(message);
    }
}
