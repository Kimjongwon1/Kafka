package Dlab.Kafka.kafka;

import Dlab.Kafka.model.ChatMessage;
import Dlab.Kafka.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageConsumerDB {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageConsumerDB.class);
    private final ChatMessageRepository chatMessageRepository;

    // HTTP 채팅 메시지 DB 저장 (브로드캐스트 없음)
    @KafkaListener(
            topics = "chat-messages-http",
            groupId = "chat-group-http-db",
            containerFactory = "chatMessageKafkaListenerFactory"
    )
    public void consumeHttpMessage(ChatMessage message) {
        chatMessageRepository.save(message);
        logger.info("HTTP 메시지 DB 저장 완료: {}", message.getContent());
    }

    // WebSocket 채팅 메시지 DB 저장
    @KafkaListener(
            topics = "chat-messages-ws",
            groupId = "chat-group-ws-db",
            containerFactory = "chatMessageKafkaListenerFactory"
    )
    public void consumeWebSocketMessage(ChatMessage message) {
        chatMessageRepository.save(message);
        logger.info("WebSocket 메시지 DB 저장 완료: {}", message.getContent());
    }
}