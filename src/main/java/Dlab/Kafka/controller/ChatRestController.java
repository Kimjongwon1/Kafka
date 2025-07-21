package Dlab.Kafka.controller;

import Dlab.Kafka.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRestController {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @PostMapping("/api/kafka/send")
    public ResponseEntity<Void> send(@RequestBody ChatMessage message) {
        kafkaTemplate.send("chat-messages", message); // Kafka로만 전송
        return ResponseEntity.ok().build();
    }
}

