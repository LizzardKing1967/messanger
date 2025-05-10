package com.project.messanger.controllers;

import com.project.messanger.entity.Message;
import com.project.messanger.service.AuthenticationService;
import com.project.messanger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/sent")
    public ResponseEntity<Message> sendTextMessage(
            @RequestParam String groupChatName,
            @RequestParam String encryptedMessageContent, Authentication authentication
            ) {
        return ResponseEntity.ok(
                messageService.sendTextMessage(authentication.getName(), groupChatName, encryptedMessageContent)
        );
    }

    @GetMapping("/{groupChatName}/history")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable String groupChatName) {
        List<Message> messages = messageService.getChatMessages(groupChatName);

        // Выводим в консоль для отладки
        messages.forEach(System.out::println);

        return ResponseEntity.ok(messages);
    }

}