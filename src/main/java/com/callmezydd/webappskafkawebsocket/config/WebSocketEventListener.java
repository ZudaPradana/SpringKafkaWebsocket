package com.callmezydd.webappskafkawebsocket.config;


import com.callmezydd.webappskafkawebsocket.model.ChatMessage;
import com.callmezydd.webappskafkawebsocket.model.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @EventListener
    public void handleWSCreate(SessionConnectedEvent sessionConnectedEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionConnectedEvent.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        String username = (sessionAttributes != null) ? (String) sessionAttributes.get("username") : null;
        if (username != null) {
            log.info("User Connected!");
            var chatMessage = ChatMessage.builder().type(MessageType.JOIN).sender(username).build();
            simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
        }
    }

    @EventListener
    public void handleWSDisconnect(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        String username = (sessionAttributes != null) ? (String) sessionAttributes.get("username") : null;
        if (username != null) {
            log.info("User Disconnected!");
            var chatMessage = ChatMessage.builder().type(MessageType.LEAVE).sender(username).build();
            simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
        }
    }
}
