package com.tolgayakar.receipt_manager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tolgayakar.receipt_manager.Model.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    public Receipt findByUserIdAndIsDeleted(Long userId, Boolean isDeleted);
}
