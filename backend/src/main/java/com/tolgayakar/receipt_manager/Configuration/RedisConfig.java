package com.tolgayakar.receipt_manager.Configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.tolgayakar.receipt_manager.Model.DTO.ReceiptResponse;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<String, List<ReceiptResponse>> redisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper) {

        RedisTemplate<String, List<ReceiptResponse>> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        JavaType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, ReceiptResponse.class);

        redisTemplate.setValueSerializer(
                new JacksonJsonRedisSerializer<List<ReceiptResponse>>(
                        objectMapper,
                        listType
                )
        );

        return redisTemplate;
    }
}