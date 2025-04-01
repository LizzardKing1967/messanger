package com.project.messanger.entity;

import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data

public class ParticipantInChat {
    private ParticipantInChatId id;
    private LocalDate creationDate;
    private String publicKey;
    private LocalDate joinDate;
    private int status;
    private String email; // новое поле


    public ParticipantInChat() {}

    public ParticipantInChat(ParticipantInChatId id, LocalDate creationDate,
                             String publicKey, LocalDate joinDate, int status) {
        this.id = id;
        this.creationDate = creationDate;
        this.publicKey = publicKey;
        this.joinDate = joinDate;
        this.status = status;
    }

    // Getters and Setters
    public ParticipantInChatId getId() {
        return id;
    }

    public void setId(ParticipantInChatId id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getEmail() {
        return email;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}