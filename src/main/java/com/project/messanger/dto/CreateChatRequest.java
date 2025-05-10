package com.project.messanger.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CreateChatRequest {
    private String chatName;
    private Map<String, String> encryptedKeys;
}
