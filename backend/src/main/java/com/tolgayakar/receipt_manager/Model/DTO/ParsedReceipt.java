package com.tolgayakar.receipt_manager.Model.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ParsedReceipt {
    private String merchantName;
    private String receiptNumber;
    private LocalDate purchaseDate;
    private BigDecimal totalAmount;
    private List<ReceiptItemDTO> items;
    
    public String getMerchantName() {
        return merchantName;
    }
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    public String getReceiptNumber() {
        return receiptNumber;
    }
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public List<ReceiptItemDTO> getItems() {
        return items;
    }
    public void setItems(List<ReceiptItemDTO> items) {
        this.items = items;
    }
}
