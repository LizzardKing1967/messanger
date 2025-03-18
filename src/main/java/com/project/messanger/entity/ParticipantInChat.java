package com.project.messanger.entity;

import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "participant_in_chat")
public class ParticipantInChat {

    @EmbeddedId
    private ParticipantInChatId id; // Составной ключ

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "status", nullable = false)
    private int status;

    // Конструкторы
    public ParticipantInChat() {}

    public ParticipantInChat(ParticipantInChatId id, LocalDateTime creationDate, String publicKey, LocalDateTime joinDate, int status) {
        this.id = id;
        this.creationDate = creationDate;
        this.publicKey = publicKey;
        this.joinDate = joinDate;
        this.status = status;

    }
}

