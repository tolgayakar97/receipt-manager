package com.tolgayakar.receipt_manager.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.tolgayakar.receipt_manager.Model.DTO.CreateReceiptRequest;
import com.tolgayakar.receipt_manager.Model.DTO.CreateReceiptResponse;
import com.tolgayakar.receipt_manager.Service.ReceiptService;

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
    public String getMethodName(@RequestParam(required = false) String param) {
        return new String();
    }
    
    @PostMapping("/receipts")
    public ResponseEntity<CreateReceiptResponse> createReceipt(@RequestBody CreateReceiptRequest createReceiptRequest) {
        //TODO: process POST request
        CreateReceiptResponse createReceiptResponse =  receiptService.createReceipt(createReceiptRequest);
        // TODO: Call OCR service
        return ResponseEntity.status(HttpStatus.CREATED).body(createReceiptResponse);
    }
}
