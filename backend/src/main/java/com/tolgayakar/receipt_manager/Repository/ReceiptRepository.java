package com.tolgayakar.receipt_manager.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tolgayakar.receipt_manager.Model.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    /**
     * Find all receipts.
     * @param userId is used to filter receipt with user id (fk)
     * @param isDeleted is used to filter receipts by provided isDeleted param.
     * @return all receipts as List<Receipt>
     */
    public List<Receipt> findByUserIdAndIsDeleted(Long userId, Boolean isDeleted);
}
