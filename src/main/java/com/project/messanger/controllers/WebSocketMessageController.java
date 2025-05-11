package com.project.messanger.controllers;

import com.project.messanger.entity.Message;

import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.service.GroupChatService;
import com.project.messanger.service.MessageService;
import com.project.messanger.service.ParticipantInChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ParticipantInChatService participantInChatService;

    @MessageMapping("/chat.sendMessage") // клиент шлёт на /app/chat.sendMessage
    public void receiveMessage(@Payload Message message) {
        // Сохраняем сообщение (или другую логику)
        Message savedMessage = messageService.sendTextMessage(
                message.getSenderUsername(),
                message.getGroupChatName(),
                message.getEncryptedTextContent()
        );

        List<ParticipantInChat> recipients = participantInChatService.getAllParticipantsInChat(message.getGroupChatName());

        for (ParticipantInChat recipient : recipients) {
            messagingTemplate.convertAndSendToUser(
                    recipient.getId().getUsername(),
                    "/queue/messages",
                    savedMessage
            );
        }
    }
}
