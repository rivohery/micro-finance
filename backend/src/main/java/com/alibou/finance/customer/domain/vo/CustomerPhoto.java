package com.alibou.finance.customer.domain.vo;

import com.alibou.finance.shared.domain.exception.IllegalArgumentException;

public record CustomerPhoto(byte[]content, String fileName) {
    public CustomerPhoto {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Photo client obligatoire");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Nom du fichier obligatoire");
        }
    }
}
