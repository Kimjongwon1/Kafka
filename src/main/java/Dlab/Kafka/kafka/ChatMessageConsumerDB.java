package Dlab.Kafka.kafka;

import Dlab.Kafka.model.ChatMessage;
import Dlab.Kafka.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageConsumerDB {

    private final ChatMessageRepository chatMessageRepository;

    @KafkaListener(
            topics = "chat-messages",
            groupId = "chat-group",
            containerFactory = "chatMessageKafkaListenerFactory"
    )
    public void consume(ChatMessage message) {
        chatMessageRepository.save(message); // ✅ 저장만
    }
}
