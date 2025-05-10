package com.project.messanger.dto;

import lombok.Data;

@Data
public class ChatNotificationDto {
    private String chatName;
    private String message;

    public ChatNotificationDto(String chatName, String message) {
        this.chatName = chatName;
        this.message = message;
    }
}
