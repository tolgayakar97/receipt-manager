package com.tolgayakar.receipt_manager.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.Receipt;
import com.tolgayakar.receipt_manager.Model.RmUser;
import com.tolgayakar.receipt_manager.Model.DTO.ReceiptRequest;
import com.tolgayakar.receipt_manager.Model.DTO.ReceiptResponse;
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

    public List<ReceiptResponse> getReceipts(Boolean isDeleted) throws UsernameNotFoundException, NoSuchElementException {
        RmUser rmUser = getRmUserFromAuth();
        List<ReceiptResponse> receiptResponsesList = new ArrayList<>();
        List<Receipt> receiptsList = receiptRepository.findByUserIdAndIsDeleted(rmUser.getId(), isDeleted).orElseThrow();

        for (Receipt receipt : receiptsList) {
            ReceiptResponse receiptResponse = new ReceiptResponse();
            receiptResponse.setId(receipt.getId());
            receiptResponse.setFilePath(receipt.getFilePath());
            receiptResponse.setName(receipt.getName());
            receiptResponse.setDescription(receipt.getDescription());
            receiptResponse.setCreatedAt(receipt.getCreatedAt());
            receiptResponsesList.add(receiptResponse);
        }

        return receiptResponsesList;
    }

    public ReceiptResponse getReceipt(Long receiptId, Boolean isDeleted) throws UsernameNotFoundException, NoSuchElementException {
        RmUser rmUser = getRmUserFromAuth();
        Receipt receipt = receiptRepository.findByIdAndUserIdAndIsDeleted(receiptId, rmUser.getId(), isDeleted).orElseThrow();

        ReceiptResponse receiptResponse = new ReceiptResponse();
        receiptResponse.setId(receipt.getId());
        receiptResponse.setFilePath(receipt.getFilePath());
        receiptResponse.setName(receipt.getName());
        receiptResponse.setDescription(receipt.getDescription());
        receiptResponse.setCreatedAt(receipt.getCreatedAt());

        return receiptResponse;
    }
    
    public ReceiptResponse createReceipt(ReceiptRequest createReceiptRequest) throws UsernameNotFoundException {
        RmUser rmUser = getRmUserFromAuth();
        
        Receipt receipt = new Receipt();
        receipt.setFilePath(createReceiptRequest.getFilePath());
        receipt.setName(createReceiptRequest.getName());
        receipt.setDescription(createReceiptRequest.getDescription());
        receipt.setDeleted(false);
        receipt.setUser(rmUser);
        Receipt savedReceipt = receiptRepository.save(receipt);

        ReceiptResponse createReceiptResponse = new ReceiptResponse();
        createReceiptResponse.setId(savedReceipt.getId());
        createReceiptResponse.setFilePath(savedReceipt.getFilePath());
        createReceiptResponse.setName(savedReceipt.getName());
        createReceiptResponse.setDescription(savedReceipt.getDescription());
        createReceiptResponse.setCreatedAt(savedReceipt.getCreatedAt());

        return createReceiptResponse;
    }

    public ReceiptResponse putReceipt(Long id, ReceiptRequest receiptRequest) throws UsernameNotFoundException, NoSuchElementException {
        RmUser rmUser = getRmUserFromAuth();
        Receipt receipt = receiptRepository.findByIdAndUserIdAndIsDeleted(id, rmUser.getId(), false).orElseThrow();
        receipt.setName(receiptRequest.getName());
        receipt.setDescription(receiptRequest.getDescription());
        receipt.setFilePath(receiptRequest.getFilePath());
        receiptRepository.save(receipt);

        ReceiptResponse receiptResponse = new ReceiptResponse();
        receiptResponse.setId(receipt.getId());
        receiptResponse.setFilePath(receipt.getFilePath());
        receiptResponse.setName(receipt.getName());
        receiptResponse.setDescription(receipt.getDescription());
        receiptResponse.setCreatedAt(receipt.getCreatedAt());

        return receiptResponse;
    }

    public void softDeleteReceipt(Long id) throws UsernameNotFoundException, NoSuchElementException {
        RmUser rmUser = getRmUserFromAuth();
        Receipt receipt = receiptRepository.findByIdAndUserIdAndIsDeleted(id, rmUser.getId(), false).orElseThrow();
        receipt.setDeleted(true);
        receiptRepository.save(receipt);
    }

    private RmUser getRmUserFromAuth() throws UsernameNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<RmUser> opt = rmUserRepository.findByEmail(auth.getName());
         if(opt.isEmpty()) {
            throw new UsernameNotFoundException("Username not found with email: " + auth.getName());
        }
        return opt.get();
    }
}
