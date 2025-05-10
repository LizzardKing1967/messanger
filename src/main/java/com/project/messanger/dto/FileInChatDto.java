package com.project.messanger.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInChatDto {
    private String username;
    private String fileName;
    private String groupChatName;
    private LocalDate creationDate;
    private String publicKey;
    private String encryptedKey;
}