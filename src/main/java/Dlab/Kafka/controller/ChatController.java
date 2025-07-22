package Dlab.Kafka.controller;

import Dlab.Kafka.kafka.ChatMessageProducer;
import Dlab.Kafka.model.ChatMessage;
import Dlab.Kafka.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageProducer chatMessageProducer;
    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/")
    public String chatPage(Model model) {
        List<ChatMessage> messages = chatMessageRepository.findAll();
        model.addAttribute("messages", messages);
        return "chat";
    }

    // HTTP 채팅 메시지 전송 (DB 저장만, 실시간 반영 안됨)
    @PostMapping("/send")
    public String sendMessage(@RequestParam String sender,
                              @RequestParam String content) {
        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        // HTTP 전용 topic으로 전송
        chatMessageProducer.sendHttpMessage(message);
        return "redirect:/";
    }

    // WebSocket 채팅 메시지 전송 (DB 저장 + 실시간 반영)
    @PostMapping("/api/send")
    @ResponseBody
    public String sendMessageApi(@RequestBody ChatMessage message) {
        if (message.getTimestamp() == null) {
            message = ChatMessage.builder()
                    .sender(message.getSender())
                    .content(message.getContent())
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // WebSocket 전용 topic으로 전송
        chatMessageProducer.sendWebSocketMessage(message);
        return "OK";
    }

    @GetMapping("/websocket")
    public String websocketPage(Model model) {
        List<ChatMessage> messages = chatMessageRepository.findAll();
        model.addAttribute("messages", messages);
        return "chat-websocket";
    }
}