package com.project.messanger.repository;

import com.project.messanger.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    boolean existsByGroupChatName(String groupChatName);
}