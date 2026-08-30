package com.tolgayakar.receipt_manager.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.tolgayakar.receipt_manager.Model.DTO.ReceiptResponse;

import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<String, ReceiptResponse> redisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper) {

        RedisTemplate<String, ReceiptResponse> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        redisTemplate.setValueSerializer(
                new JacksonJsonRedisSerializer<>(
                        objectMapper,
                        ReceiptResponse.class
                )
        );

        return redisTemplate;
    }
}