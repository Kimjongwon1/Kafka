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
    private static final String TOPIC = "chat-messages";

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public void sendMessage(ChatMessage message) {
        logger.info("Kafka 전송: {} - {}", message.getSender(), message.getContent());
        kafkaTemplate.send(TOPIC, message);
    }
}
