package com.project.messanger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FileInChat {

    private String username;


    private String fileName;


    private String groupChatName;

    private LocalDateTime creationDate;

}