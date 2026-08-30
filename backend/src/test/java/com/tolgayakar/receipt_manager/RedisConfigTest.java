package com.tolgayakar.receipt_manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.tolgayakar.receipt_manager.Model.DTO.ReceiptResponse;

@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, ReceiptResponse> redisTemplate;

    @Test
    void shouldWriteAndReadReceiptResponse() {

        ReceiptResponse receiptResponse = new ReceiptResponse();
        receiptResponse.setId(1L);
        receiptResponse.setName("Yunus Market");
        receiptResponse.setDescription("Test receipt");
        receiptResponse.setFilePath("/test/receipt.jpg");
        receiptResponse.setCreatedAt(Instant.now());

        redisTemplate.opsForValue().set("test:receipt", receiptResponse);

        ReceiptResponse cachedReceiptResponse =
                redisTemplate.opsForValue().get("test:receipt");

        assertEquals(receiptResponse.getId(), cachedReceiptResponse.getId());
        assertEquals(receiptResponse.getName(), cachedReceiptResponse.getName());
        assertEquals(receiptResponse.getDescription(), cachedReceiptResponse.getDescription());

        redisTemplate.delete("test:receipt");
    }
}