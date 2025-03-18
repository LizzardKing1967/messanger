package com.project.messanger.entity.compoundKeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class ParticipantInChatId implements Serializable {

    @Column(name = "group_chat_name", nullable = false)
    private String groupChatName;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "role_name", nullable = false)
    private String role_name;

    // Геттеры, сеттеры, equals и hashCode
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

    public void setRole(String role) {
        this.role_name = role;
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

    public void setRole_name(String role) {
        this.role_name =role;
    }
}