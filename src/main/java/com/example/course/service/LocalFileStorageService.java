package com.example.course.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        // Unikal fayl nomini yaratish
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        Path targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
        // Agar papka mavjud bo'lmasa, yaratish
        Files.createDirectories(targetLocation.getParent());
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        // Saqlangan faylga URL yoki yo'lni qaytarish (bu yerda oddiy yo‘l)
        return "/uploads/" + fileName;
    }
}
