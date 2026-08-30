package com.tolgayakar.receipt_manager.Configuration;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import com.tolgayakar.receipt_manager.Model.Event.ReceiptCreatedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
        @Bean
        ConsumerFactory<String, ReceiptCreatedEvent> consumerFactory() {
                Map<String, Object> config = new HashMap<>();

                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
                config.put(ConsumerConfig.GROUP_ID_CONFIG, "receipt-manager");
                config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

                return new DefaultKafkaConsumerFactory<>(
                        config,
                        new StringDeserializer(),
                        new JacksonJsonDeserializer<>(ReceiptCreatedEvent.class));
        }

        @Bean
        ConcurrentKafkaListenerContainerFactory<String, ReceiptCreatedEvent> kafkaListenerContainerFactory(
                        ConsumerFactory<String, ReceiptCreatedEvent> consumerFactory) {

                ConcurrentKafkaListenerContainerFactory<String, ReceiptCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

                factory.setConsumerFactory(consumerFactory);

                return factory;
        }

        @Bean
        ProducerFactory<String, ReceiptCreatedEvent> producerFactory() {
                Map<String, Object> config = new HashMap<>();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,JacksonJsonSerializer.class);

                return new DefaultKafkaProducerFactory<>(config);
        }

        @Bean
        KafkaTemplate<String, ReceiptCreatedEvent> kafkaTemplate(
                        ProducerFactory<String, ReceiptCreatedEvent> producerFactory) {

                return new KafkaTemplate<>(producerFactory);
        }

        @Bean
        KafkaAdmin kafkaAdmin() {
                Map<String, Object> config = new HashMap<>();
                config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
                return new KafkaAdmin(config);
        }

        @Bean
        NewTopic receiptCreatedTopic() {
                return new NewTopic("receipt-created", 1, (short) 1);
        }
}