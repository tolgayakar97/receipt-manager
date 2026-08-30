package com.tolgayakar.receipt_manager.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tolgayakar.receipt_manager.Model.Receipt;
import com.tolgayakar.receipt_manager.Model.ReceiptItem;
import com.tolgayakar.receipt_manager.Model.RmUser;
import com.tolgayakar.receipt_manager.Model.DTO.OcrResponse;
import com.tolgayakar.receipt_manager.Model.DTO.ParsedReceipt;
import com.tolgayakar.receipt_manager.Model.DTO.ReceiptItemDTO;
import com.tolgayakar.receipt_manager.Model.DTO.ReceiptRequest;
import com.tolgayakar.receipt_manager.Model.DTO.ReceiptResponse;
import com.tolgayakar.receipt_manager.Model.Event.ReceiptCreatedEvent;
import com.tolgayakar.receipt_manager.Repository.ReceiptRepository;
import com.tolgayakar.receipt_manager.Repository.RmUserRepository;
import com.tolgayakar.receipt_manager.Service.Kafka.ReceiptEventProducer;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final RmUserRepository rmUserRepository;
    private final OcrClient ocrClient;
    private final RedisTemplate<String, List<ReceiptResponse>> redisTemplate;
    private final ReceiptEventProducer receiptEventProducer;
    private final FileStorageService fileStorageService;

    public ReceiptService(ReceiptRepository receiptRepository, RmUserRepository rmUserRepository, OcrClient ocrrClient,
            RedisTemplate<String, List<ReceiptResponse>> redisTemplate, ReceiptEventProducer receiptEventProducer,
            FileStorageService fileStorageService) {
        this.receiptRepository = receiptRepository;
        this.rmUserRepository = rmUserRepository;
        this.ocrClient = ocrrClient;
        this.redisTemplate = redisTemplate;
        this.receiptEventProducer = receiptEventProducer;
        this.fileStorageService = fileStorageService;
    }

    public List<ReceiptResponse> getReceipts(Boolean isDeleted)
            throws UsernameNotFoundException, NoSuchElementException {
        RmUser rmUser = getRmUserFromAuth();

        String cacheKey = "receipts:user:" + rmUser.getId() + ":" + isDeleted;

        List<ReceiptResponse> cachedResponsesList = redisTemplate.opsForValue().get(cacheKey);
        if (cachedResponsesList != null) {
            return cachedResponsesList;
        }

        List<ReceiptResponse> receiptResponsesList = new ArrayList<>();
        List<Receipt> receiptsList = receiptRepository.findByUserIdAndIsDeleted(rmUser.getId(), isDeleted)
                .orElseThrow();

        for (Receipt receipt : receiptsList) {
            ReceiptResponse receiptResponse = new ReceiptResponse();
            receiptResponse.setId(receipt.getId());
            receiptResponse.setFilePath(receipt.getFilePath());
            receiptResponse.setName(receipt.getName());
            receiptResponse.setDescription(receipt.getDescription());
            receiptResponse.setCreatedAt(receipt.getCreatedAt());
            receiptResponsesList.add(receiptResponse);
        }

        redisTemplate.opsForValue().set(cacheKey, receiptResponsesList);
        return receiptResponsesList;
    }

    public ReceiptResponse getReceipt(Long receiptId, Boolean isDeleted)
            throws UsernameNotFoundException, NoSuchElementException {
        RmUser rmUser = getRmUserFromAuth();
        Receipt receipt = receiptRepository.findByIdAndUserIdAndIsDeleted(receiptId, rmUser.getId(), isDeleted)
                .orElseThrow();

        ReceiptResponse receiptResponse = new ReceiptResponse();
        receiptResponse.setId(receipt.getId());
        receiptResponse.setFilePath(receipt.getFilePath());
        receiptResponse.setName(receipt.getName());
        receiptResponse.setDescription(receipt.getDescription());
        receiptResponse.setCreatedAt(receipt.getCreatedAt());

        return receiptResponse;
    }

    public ReceiptResponse createReceipt(ReceiptRequest receiptRequest) throws UsernameNotFoundException {
        RmUser rmUser = getRmUserFromAuth();

        String filePath;

        try {
            filePath = fileStorageService.saveFile(receiptRequest.getFile());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save receipt file", e);
        }

        Receipt receipt = new Receipt();
        receipt.setFilePath(filePath);
        receipt.setName(receiptRequest.getName());
        receipt.setDescription(receiptRequest.getDescription());
        receipt.setDeleted(false);
        receipt.setUser(rmUser);
        Receipt savedReceipt = receiptRepository.save(receipt);

        receiptEventProducer.sendReceiptCreateEvent(
                new ReceiptCreatedEvent(savedReceipt.getId(), rmUser.getId(), savedReceipt.getFilePath()));

        ReceiptResponse receiptResponse = new ReceiptResponse();
        receiptResponse.setId(savedReceipt.getId());
        receiptResponse.setFilePath(savedReceipt.getFilePath());
        receiptResponse.setName(savedReceipt.getName());
        receiptResponse.setDescription(savedReceipt.getDescription());
        receiptResponse.setCreatedAt(savedReceipt.getCreatedAt());

        return receiptResponse;
    }

    public ReceiptResponse putReceipt(Long id, ReceiptRequest receiptRequest)
            throws UsernameNotFoundException, NoSuchElementException {
        RmUser rmUser = getRmUserFromAuth();
        Receipt receipt = receiptRepository.findByIdAndUserIdAndIsDeleted(id, rmUser.getId(), false).orElseThrow();
        receipt.setName(receiptRequest.getName());
        receipt.setDescription(receiptRequest.getDescription());
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
        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("Username not found with email: " + auth.getName());
        }
        return opt.get();
    }

    public void performOcr(MultipartFile file, Receipt receipt) {
        try {
            OcrResponse ocrResponse = ocrClient.process(file);
            ParsedReceipt parsedReceipt = ocrResponse.getParsedReceipt();
            persistDb(receipt, parsedReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void persistDb(Receipt receipt, ParsedReceipt parsedReceipt) {
        receipt.setMerchantName(parsedReceipt.getMerchantName());
        receipt.setReceiptNumber(parsedReceipt.getReceiptNumber());
        receipt.setPurchaseDate(parsedReceipt.getPurchaseDate());
        receipt.setTotalAmount(parsedReceipt.getTotalAmount());

        for (ReceiptItemDTO receiptItemDto : parsedReceipt.getItems()) {
            ReceiptItem receiptItem = new ReceiptItem();
            receiptItem.setName(receiptItemDto.getName());
            receiptItem.setQuantity(receiptItemDto.getQuantity());
            receiptItem.setUnit(receiptItemDto.getUnit());
            receiptItem.setUnitPrice(receiptItemDto.getUnitPrice());

            receipt.addReceiptItem(receiptItem);
        }

        receiptRepository.save(receipt);
    }

    public Receipt getReceiptForProcessing(Long receiptId) {
        return receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Receipt not found: " + receiptId));
    }
}
