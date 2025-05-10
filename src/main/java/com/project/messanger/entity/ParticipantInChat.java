package com.project.messanger.entity;

import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import lombok.Data;

import java.time.LocalDate;

@Data

public class ParticipantInChat {
    private ParticipantInChatId id;
    private String role_name;
    private String encryptedKey;
    private LocalDate joinDate;
    private int status;
    private String keyVersion;



    public ParticipantInChat() {}

    public ParticipantInChat(ParticipantInChatId id, String role_name,
                             String publicKey, LocalDate joinDate, int status, String keyVersion) {
        this.id = id;
        this.role_name = role_name;
        this.encryptedKey = publicKey;
        this.joinDate = joinDate;
        this.status = status;
        this.keyVersion = keyVersion;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    // Getters and Setters
    public ParticipantInChatId getId() {
        return id;
    }

    public void setId(String groupChatName, String username) {
        this.id.setUsername(username);
        this.id.setGroupChatName(groupChatName);
    }


    public void setId(ParticipantInChatId id) {
        this.id = id;
    }


    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }


    public String getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(String keyVersion) {
        this.keyVersion = keyVersion;
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