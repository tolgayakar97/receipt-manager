package com.tolgayakar.receipt_manager.Service.Kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.Event.ReceiptCreatedEvent;

@Service
public class ReceiptEventProducer {
    //Spring Boot's helper class that enables us to send messages to Kafka.
    private final KafkaTemplate<String, ReceiptCreatedEvent> kafkaTemplate;

    @Value("${app.kafka.topic.receipt-created}")
    private String topic; // Kafka channel/topic

    public ReceiptEventProducer(KafkaTemplate<String, ReceiptCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReceiptCreateEvent(ReceiptCreatedEvent receiptCreatedEvent) {
        kafkaTemplate.send(topic, receiptCreatedEvent).whenComplete((result, exception) -> {

                    if (exception != null) {
                        System.out.println("Kafka event gönderilemedi: " + exception.getMessage());
                    } else {
                        System.out.println(
                            "Kafka event gönderildi! topic="
                            + result.getRecordMetadata().topic()
                            + ", partition="
                            + result.getRecordMetadata().partition()
                            + ", offset="
                            + result.getRecordMetadata().offset()
                        );
                    }
                });;
    }
}
