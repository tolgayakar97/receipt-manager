package com.tolgayakar.receipt_manager.Service.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tolgayakar.receipt_manager.Model.Event.ReceiptCreatedEvent;

@Service
public class ReceiptEventConsumer {

    public ReceiptEventConsumer() {
        System.out.println("ReceiptEventConsumer bean oluşturuldu!");
    }

    @KafkaListener(topics = "${app.kafka.topic.receipt-created}", groupId = "${app.kafka.consumer.group-id}")
    public void consumerReceiptCreatedEvent(ReceiptCreatedEvent receiptCreatedEvent) {
        System.out.println("Event: " + receiptCreatedEvent);
    }
}
