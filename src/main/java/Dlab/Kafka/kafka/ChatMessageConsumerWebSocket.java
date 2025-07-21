package Dlab.Kafka.kafka;

import Dlab.Kafka.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageConsumerWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageConsumerWebSocket.class);
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "chat-messages",
            groupId = "chat-group-ws",
            containerFactory = "chatMessageKafkaListenerFactory"
    )
    public void consume(ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/messages", message);
        logger.info("WebSocket 전송 완료: {}", message.getContent());
    }
}
