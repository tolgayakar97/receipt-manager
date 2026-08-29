package com.tolgayakar.receipt_manager.Model.DTO;

import java.util.List;

public class OcrResponse {
    private String filename;
    private List<String> texts;
    private ParsedReceipt parsedReceipt;

    public ParsedReceipt getParsedReceipt() {
        return parsedReceipt;
    }

    public void setParsedReceipt(ParsedReceipt parsedReceipt) {
        this.parsedReceipt = parsedReceipt;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getTexts() {
        return texts;
    }
    
    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}
