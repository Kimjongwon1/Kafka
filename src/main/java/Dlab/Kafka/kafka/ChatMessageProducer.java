package Dlab.Kafka.kafka;

import Dlab.Kafka.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageProducer.class);
    private static final String HTTP_TOPIC = "chat-messages-http";      // HTTP 전용
    private static final String WEBSOCKET_TOPIC = "chat-messages-ws";   // WebSocket 전용

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    // HTTP 채팅용 (DB 저장만)
    public void sendHttpMessage(ChatMessage message) {
        logger.info("HTTP Kafka 전송: {} - {}", message.getSender(), message.getContent());
        kafkaTemplate.send(HTTP_TOPIC, message);
    }

    // WebSocket 채팅용 (DB 저장 + 실시간 브로드캐스트)
    public void sendWebSocketMessage(ChatMessage message) {
        logger.info("WebSocket Kafka 전송: {} - {}", message.getSender(), message.getContent());
        kafkaTemplate.send(WEBSOCKET_TOPIC, message);
    }

    // 기존 메서드는 호환성을 위해 유지 (WebSocket으로 처리)
//    public void sendMessage(ChatMessage message) {
//        sendWebSocketMessage(message);
//    }
}