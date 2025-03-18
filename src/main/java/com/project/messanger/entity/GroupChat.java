package com.project.messanger.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "group_chat")
public class GroupChat {

    public GroupChat() {}
    public GroupChat(String groupChatName, LocalDateTime creationDate, String publicKey) {
        this.groupChatName = groupChatName;
        this.creationDate = creationDate;
        this.publicKey = publicKey;
    }

    @Id
    @Column(nullable = false, unique = true)
    private String groupChatName;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false, unique = true)
    private String publicKey;


}

