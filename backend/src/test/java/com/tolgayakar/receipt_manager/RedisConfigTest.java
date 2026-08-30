package com.tolgayakar.receipt_manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.tolgayakar.receipt_manager.Model.DTO.ReceiptResponse;

@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, List<ReceiptResponse>> redisTemplate;

    @Test
    void shouldWriteAndReadReceiptResponseList() {

        ReceiptResponse receiptResponse = new ReceiptResponse();
        receiptResponse.setId(1L);
        receiptResponse.setName("Yunus Market");
        receiptResponse.setDescription("Test receipt");
        receiptResponse.setFilePath("/test/receipt.jpg");
        receiptResponse.setCreatedAt(Instant.now());

        List<ReceiptResponse> receiptResponses =
                List.of(receiptResponse);

        redisTemplate.opsForValue().set("test:receipts", receiptResponses);

        List<ReceiptResponse> cachedReceiptResponses =
                redisTemplate.opsForValue().get("test:receipts");

        assertEquals(1, cachedReceiptResponses.size());

        ReceiptResponse cachedReceiptResponse =
                cachedReceiptResponses.get(0);

        assertEquals(receiptResponse.getId(), cachedReceiptResponse.getId());
        assertEquals(receiptResponse.getName(), cachedReceiptResponse.getName());
        assertEquals(receiptResponse.getDescription(), cachedReceiptResponse.getDescription());
        assertEquals(receiptResponse.getFilePath(), cachedReceiptResponse.getFilePath());

        redisTemplate.delete("test:receipts");
    }
}