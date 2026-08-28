package com.tolgayakar.receipt_manager.Service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.Receipt;
import com.tolgayakar.receipt_manager.Model.RmUser;
import com.tolgayakar.receipt_manager.Model.DTO.CreateReceiptRequest;
import com.tolgayakar.receipt_manager.Model.DTO.CreateReceiptResponse;
import com.tolgayakar.receipt_manager.Repository.ReceiptRepository;
import com.tolgayakar.receipt_manager.Repository.RmUserRepository;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final RmUserRepository rmUserRepository;

    public ReceiptService(ReceiptRepository receiptRepository, RmUserRepository rmUserRepository) {
        this.receiptRepository = receiptRepository;
        this.rmUserRepository = rmUserRepository;
    }
    
    public CreateReceiptResponse createReceipt(CreateReceiptRequest createReceiptRequest) throws UsernameNotFoundException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<RmUser> opt = rmUserRepository.findByEmail(auth.getName());
        if(opt.isEmpty()) {
            throw new UsernameNotFoundException("Username not found with email: " + auth.getName());
        }

        Receipt receipt = new Receipt();
        receipt.setFilePath(createReceiptRequest.getFilePath());
        receipt.setName(createReceiptRequest.getName());
        receipt.setDescription(createReceiptRequest.getDescription());
        receipt.isDeleted(false);
        receipt.setUser(opt.get());
        Receipt savedReceipt = receiptRepository.save(receipt);

        CreateReceiptResponse createReceiptResponse = new CreateReceiptResponse();
        createReceiptResponse.setId(savedReceipt.getId());
        createReceiptResponse.setFilePath(savedReceipt.getFilePath());
        createReceiptResponse.setName(savedReceipt.getName());
        createReceiptResponse.setDescription(savedReceipt.getDescription());
        createReceiptResponse.setCreatedAt(savedReceipt.getCreatedAt());

        return createReceiptResponse;
    }
}
