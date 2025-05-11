package com.project.messanger.service;

import com.project.messanger.entity.Message;
import com.project.messanger.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message sendTextMessage(String username, String groupChatName, String encryptedMessageContent) {
        Message message = new Message();
        message.setSenderUsername(username);
        message.setGroupChatName(groupChatName);
        message.setEncryptedTextContent(encryptedMessageContent);
        message.setSendDate(LocalDateTime.now());
        message.setStatus(1); // Статус "доставлено"

        return messageRepository.save(message);
    }

    public List<Message> getChatMessages(String groupChatName) {
        return messageRepository.findByGroupChatName(groupChatName);
    }


}