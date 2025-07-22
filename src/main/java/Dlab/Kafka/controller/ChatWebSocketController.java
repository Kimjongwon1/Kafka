package Dlab.Kafka.controller;

import Dlab.Kafka.kafka.ChatMessageProducer;
import Dlab.Kafka.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageProducer chatMessageProducer;

    @MessageMapping("/chat/send")
    public void sendViaWebSocket(ChatMessage message) {
        // WebSocket으로 받은 메시지는 WebSocket topic으로 전송
        chatMessageProducer.sendWebSocketMessage(message);
    }
}