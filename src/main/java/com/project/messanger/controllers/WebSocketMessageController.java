package com.project.messanger.controller;

import com.project.messanger.entity.Message;
import com.project.messanger.model.Message;
import com.project.messanger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage") // клиент будет слать на /app/send-message
    public void receiveMessage(Message message) {
        Message saved = messageService.sendTextMessage(message.getSenderUsername(), message.getGroupChatName(),message.getEncryptedTextContent());
        messagingTemplate.convertAndSend("/topic/messages/" + message.getGroupChatName(), saved);
    }
}
