package com.project.messanger.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupChat {
    private String groupChatName;
    private LocalDateTime creationDate;

    public GroupChat() {}

    public GroupChat(String groupChatName, LocalDateTime creationDate) {
        this.groupChatName = groupChatName;
        this.creationDate = creationDate;
    }


    // Getters and Setters
    public String getGroupChatName() {
        return groupChatName;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

}