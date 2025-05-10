package com.project.messanger.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String senderUsername;
    private String groupChatName;
    private String encryptedTextContent;
    private LocalDateTime sendDate;
    private int status;

    @JsonProperty("encryptedContent")
    public String getEncryptedTextContent() {
        return encryptedTextContent;
    }
}
