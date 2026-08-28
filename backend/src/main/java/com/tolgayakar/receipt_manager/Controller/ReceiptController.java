package com.tolgayakar.receipt_manager.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.tolgayakar.receipt_manager.Model.DTO.CreateReceiptRequest;
import com.tolgayakar.receipt_manager.Model.DTO.ReceiptResponse;
import com.tolgayakar.receipt_manager.Service.ReceiptService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }
    
    @GetMapping("/receipts")
    public ResponseEntity<List<ReceiptResponse>> getReceipts(@RequestParam Boolean isDeleted) {
        List<ReceiptResponse> receiptsList =  receiptService.getReceipts(isDeleted);
        return ResponseEntity.ok(receiptsList);
    }
    
    @PostMapping("/receipts")
    public ResponseEntity<ReceiptResponse> createReceipt(@RequestBody CreateReceiptRequest createReceiptRequest) {
        ReceiptResponse createReceiptResponse =  receiptService.createReceipt(createReceiptRequest);
        // TODO: Call OCR service
        return ResponseEntity.status(HttpStatus.CREATED).body(createReceiptResponse);
    }
}
