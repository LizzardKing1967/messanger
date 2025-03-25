package com.project.messanger.entity.compoundKeys;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data

public class ParticipantInChatId implements Serializable {
    private String groupChatName;
    private String username;
    private String role_name;

    public String getGroupChatName() {
        return groupChatName;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantInChatId that = (ParticipantInChatId) o;
        return Objects.equals(groupChatName, that.groupChatName) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupChatName, username);
    }
}