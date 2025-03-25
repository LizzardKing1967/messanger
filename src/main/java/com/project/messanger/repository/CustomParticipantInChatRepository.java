package com.project.messanger.repository;

import com.project.messanger.entity.ParticipantInChat;

import java.util.List;

public interface CustomParticipantInChatRepository {
    List<ParticipantInChat> findByDynamicQuery(String username, String role, String groupChat);
}