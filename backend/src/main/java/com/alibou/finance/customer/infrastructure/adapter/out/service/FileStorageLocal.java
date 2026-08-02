package com.alibou.finance.customer.infrastructure.adapter.out.service;

import com.alibou.finance.customer.domain.out.service.FileStoragePort;
import com.alibou.finance.shared.error.domain.FileUploadedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static java.io.File.separator;

@Service
public class FileStorageLocal implements FileStoragePort {

    private static final String UPLOAD_RIR = "./uploads";

    @Override
    public String uploadFile(byte[] fileContent, String fileName, String subPath) {

        String dirUploadPath = createDir(subPath);

        String fileExtension = getFileExtension(fileName);

        String targetFilePath = dirUploadPath + separator + UUID.randomUUID() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, fileContent);
            return targetFilePath;
        } catch (IOException e) {
            throw new FileUploadedException("Une erreur se produit lors du copie de fichier");
        }
    }

    private String createDir(String subPath){
        if(subPath == null || subPath.isEmpty()) {
            subPath = "images";
        }
        final String finalUploadPath = UPLOAD_RIR + separator + subPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                throw new FileUploadedException("Failed to create the target folder: " + targetFolder);
            }
        }
        return finalUploadPath;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
