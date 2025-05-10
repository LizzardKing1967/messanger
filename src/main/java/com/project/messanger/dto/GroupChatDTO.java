package com.project.messanger.dto;

import com.project.messanger.entity.FileInChat;
import com.project.messanger.entity.GroupChat;
import com.project.messanger.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class GroupChatDTO {

    private String name;

    private Boolean groupType;

    private String lastMessageSender;

    private String lastMessage;

    private LocalDateTime lastMessageDate;

    private LocalDateTime groupCreationDate; // 🔹 Добавлено для вторичной сортировки

    public GroupChatDTO(GroupChat groupChat, Message lastMessage) {
        this.name = groupChat.getGroupChatName();
        this.groupType = true; // или как у тебя по логике
        this.lastMessageSender = lastMessage != null ? lastMessage.getSenderUsername() : null;
        this.lastMessage = lastMessage != null ? lastMessage.getEncryptedTextContent() : "Чат создан";
        this.lastMessageDate = lastMessage != null ? lastMessage.getSendDate() : null;
        this.groupCreationDate = groupChat.getCreationDate();
    }

    public GroupChatDTO(String name, Boolean groupType, String lastMessageSender,
                        String lastMessage, LocalDateTime lastMessageDate, LocalDateTime groupCreationDate) {
        this.name = name;
        this.groupType = groupType;
        this.lastMessageSender = lastMessageSender;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
        this.groupCreationDate = groupCreationDate;
    }
}