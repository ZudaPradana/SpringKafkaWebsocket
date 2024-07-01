package com.callmezydd.webappskafkawebsocket.services;

import com.callmezydd.webappskafkawebsocket.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KafkaConsumerServices {

    private static final String TOPIC = "public-chats"; // Ganti dengan nama topik yang Anda inginkan

    private final SimpMessagingTemplate messagingTemplate;
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    public KafkaConsumerServices(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = TOPIC, groupId = "my-group")
    public void handleMessage(ChatMessage message) {
        log.info("Received message from Kafka: {}", message);
        chatMessages.add(message);
        messagingTemplate.convertAndSend("/topic/public", message);
    }

    public List<ChatMessage> getChatMessages() {
        // Implementasikan logika untuk mengembalikan daftar pesan dari Kafka
        return new ArrayList<>(chatMessages);
    }
}
