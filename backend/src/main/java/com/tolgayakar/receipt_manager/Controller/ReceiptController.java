package com.tolgayakar.receipt_manager.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.tolgayakar.receipt_manager.Model.DTO.ReceiptRequest;
import com.tolgayakar.receipt_manager.Model.DTO.ReceiptResponse;
import com.tolgayakar.receipt_manager.Service.OcrClient;
import com.tolgayakar.receipt_manager.Service.ReceiptService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class ReceiptController {

    private final ReceiptService receiptService;
    private final OcrClient ocrClient;

    public ReceiptController(ReceiptService receiptService, OcrClient ocrClient) {
        this.receiptService = receiptService;
        this.ocrClient = ocrClient;
    }
    
    @GetMapping("/receipts")
    public ResponseEntity<List<ReceiptResponse>> getReceipts(@RequestParam Boolean isDeleted) {
        List<ReceiptResponse> receiptsList =  receiptService.getReceipts(isDeleted);
        return ResponseEntity.ok(receiptsList);
    }
    
    @GetMapping("/receipts/{id}")
    public ResponseEntity<ReceiptResponse>  getReceipt(@PathVariable Long id, @RequestParam Boolean isDeleted) {
        // TODO: Add global exception handler
        ReceiptResponse receiptResponse = receiptService.getReceipt(id, isDeleted);
        return ResponseEntity.ok(receiptResponse);
    }

    /**
     * ReqeustBody annotation is used for getting JSON data from request body.
     * In order to upload file, Multipart/form-data request is used.
     * ModelAttribute annotation is used to get multipart/form-data request and bind it to ReceiptRequest DTO.
     * @param ReceiptRequest
     * @return
     */
    @PostMapping("/receipts")
    public ResponseEntity<ReceiptResponse> createReceipt(@ModelAttribute ReceiptRequest receiptRequest) {
        ReceiptResponse createReceiptResponse =  receiptService.createReceipt(receiptRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createReceiptResponse);
    }

    @PutMapping("/receipts/{id}")
    public ResponseEntity<ReceiptResponse> putReceipt(@PathVariable Long id, @RequestBody ReceiptRequest receiptRequest) {
        ReceiptResponse receiptResponse =  receiptService.putReceipt(id, receiptRequest);
        return ResponseEntity.ok(receiptResponse);
    }

    @DeleteMapping("/receipts/{id}")
    public ResponseEntity<Void> softDeleteReceipt(@PathVariable Long id) {
        receiptService.softDeleteReceipt(id);
        return ResponseEntity.noContent().build();
    }
}
