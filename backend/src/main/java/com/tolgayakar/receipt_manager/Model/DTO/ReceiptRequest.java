package com.tolgayakar.receipt_manager.Model.DTO;

import org.springframework.web.multipart.MultipartFile;

public class ReceiptRequest {
    // In. order to get real receipt file, there is a request type: multipart/form-data.
    // MultipartFile represents the uploaded file through a multipart/form-data request.
    private MultipartFile file;
    private String filePath;
    private String name;
    private String description;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
