package com.tolgayakar.receipt_manager.Model.Event;

public record ReceiptCreatedEvent(Long receiptId, Long userId, String filePath) {}