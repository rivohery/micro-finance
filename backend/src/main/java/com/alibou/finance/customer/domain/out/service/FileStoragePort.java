package com.alibou.finance.customer.domain.out.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String uploadFile(byte[] fileContent, String fileName, String subPath);

}
