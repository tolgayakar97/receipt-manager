package com.tolgayakar.receipt_manager.Service.Kafka;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.Receipt;
import com.tolgayakar.receipt_manager.Model.DTO.OcrResponse;
import com.tolgayakar.receipt_manager.Model.Event.ReceiptCreatedEvent;
import com.tolgayakar.receipt_manager.Service.OcrClient;
import com.tolgayakar.receipt_manager.Service.ReceiptService;

@Service
public class ReceiptEventConsumer {

    private final OcrClient ocrClient;
    private final ReceiptService receiptService;

    public ReceiptEventConsumer(OcrClient ocrClient, ReceiptService receiptService) {
        this.ocrClient = ocrClient;
        this.receiptService = receiptService;
        System.out.println("ReceiptEventConsumer bean oluşturuldu!");
    }

    @KafkaListener(topics = "${app.kafka.topic.receipt-created}", groupId = "${app.kafka.consumer.group-id}")
    public void consumerReceiptCreatedEvent(ReceiptCreatedEvent receiptCreatedEvent) {
        Receipt receipt = receiptService.getReceiptForProcessing(receiptCreatedEvent.receiptId());
        Path filePath = Paths.get(receipt.getFilePath());
        try {
            OcrResponse ocrResponse = ocrClient.process(filePath);
            receiptService.persistDb(receipt, ocrResponse.getParsedReceipt());
            System.out.println("OCR completed: " + ocrResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
