package Dlab.Kafka.controller;

import Dlab.Kafka.kafka.ChatMessageProducer;
import Dlab.Kafka.model.ChatMessage;
import Dlab.Kafka.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

    // 채팅 입력 페이지
    @GetMapping("/")
    public String chatPage(Model model) {
        List<ChatMessage> messages = chatMessageRepository.findAll();
        model.addAttribute("messages", messages);
        return "chat"; // templates/chat.html 로 렌더링
    }

    // 채팅 메시지 전송 (form 방식)
    @PostMapping("/send")
    public String sendMessage(@RequestParam String sender,
                              @RequestParam String content) {
        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageProducer.sendMessage(message);
        return "redirect:/";
    }

    // (선택) JSON 방식 전송 API
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
        chatMessageProducer.sendMessage(message);
        return "OK";
    }

    // (선택) 모든 메시지 조회 API
    @GetMapping("/api/messages")
    @ResponseBody
    public List<ChatMessage> getMessages() {
        return chatMessageRepository.findAll();
    }

    @GetMapping("/websocket")
    public String websocketPage(Model model) {
        List<ChatMessage> messages = chatMessageRepository.findAll();
        model.addAttribute("messages", messages);
        return "chat-websocket";  // templates/chat-websocket.html
    }

}
