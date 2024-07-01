package com.callmezydd.webappskafkawebsocket.controller;

import com.callmezydd.webappskafkawebsocket.model.ChatMessage;
import com.callmezydd.webappskafkawebsocket.services.KafkaConsumerServices;
import com.callmezydd.webappskafkawebsocket.services.KafkaProducerServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class WebSocketController {

    private final KafkaProducerServices kafkaProducerServices;
    private final KafkaConsumerServices kafkaConsumerServices;

    public WebSocketController(KafkaProducerServices kafkaProducerServices, KafkaConsumerServices kafkaConsumerServices) {
        this.kafkaProducerServices = kafkaProducerServices;
        this.kafkaConsumerServices = kafkaConsumerServices;
    }


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public void handleChatMessage(@Payload ChatMessage message) {
        // Send the message to Kafka
        kafkaProducerServices.sendMessage(message);
//        return message;
    }


    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public void addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor){
        //add user to join room chat
        log.info("User added: {}", message.getSender());

        if (headerAccessor != null && headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", message.getSender());
        } else {
            log.error("headerAccessor or session attributes is null.");
        }
        kafkaProducerServices.sendMessage(message);
//        return message;
    }

    @MessageMapping("/chat.removeUser")
    @SendTo("/topic/public")
    public void removeUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // Menangani pengguna yang keluar (disconnect)
        log.info("User disconnected: {}", message.getSender());

        // Mengirim pesan ke topik "/topic/public"
        kafkaProducerServices.sendMessage(message);

        if (headerAccessor != null && headerAccessor.getSessionAttributes() != null) {
            // Hapus atribut username dari sesi
            headerAccessor.getSessionAttributes().remove("username");
        } else {
            log.error("headerAccessor or session attributes is null.");
        }
    }

    @GetMapping("/api/chat")
    public List<ChatMessage> getChatMessages() {
        // Ambil data chat dari Kafka dan kirimkan ke frontend
        return kafkaConsumerServices.getChatMessages();
    }
}
