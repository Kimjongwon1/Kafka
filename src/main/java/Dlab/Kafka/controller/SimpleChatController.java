package Dlab.Kafka.controller;

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
public class SimpleChatController {

    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/simple")
    public String chatPage(Model model) {
        List<ChatMessage> messages = chatMessageRepository.findAll();
        model.addAttribute("messages", messages);
        return "simple-chat"; // resources/templates/simple-chat.html
    }

    @PostMapping("/simple/send")
    public String sendMessage(@RequestParam String sender,
                              @RequestParam String content) {
        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
        return "redirect:/simple";
    }
}
