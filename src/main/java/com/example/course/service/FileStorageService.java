package com.example.course.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Faylni qabul qilib, uni serverda saqlaydi va saqlangan faylning URL yoki yo‘lini qaytaradi.
     */
    String storeFile(MultipartFile file) throws IOException;
}
