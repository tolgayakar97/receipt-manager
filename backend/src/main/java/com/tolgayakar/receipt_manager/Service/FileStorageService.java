package com.tolgayakar.receipt_manager.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    private final Path uploadDir = Paths.get("/app/uploads");

    public String saveFile(MultipartFile file) throws IOException {
        Files.createDirectories(uploadDir);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = uploadDir.resolve(fileName);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return target.toString();
    }
}
