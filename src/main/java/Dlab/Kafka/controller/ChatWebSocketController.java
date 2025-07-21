package Dlab.Kafka.controller;

import Dlab.Kafka.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void sendViaWebSocket(ChatMessage message) {
        kafkaTemplate.send("chat-messages", message); // Kafka 전송
        messagingTemplate.convertAndSend("/topic/messages", message); // WebSocket 브로드캐스트
    }
}

