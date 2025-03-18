package com.project.messanger.service;

import com.project.messanger.entity.GroupChat;
import com.project.messanger.repository.GroupChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;

    public GroupChatService(GroupChatRepository groupChatRepository) {
        this.groupChatRepository = groupChatRepository;
    }

    // Создать новый групповой чат
    @Transactional
    public void createGroupChat(String groupChatName, String publicKey) {
        if (groupChatRepository.existsByGroupChatName(groupChatName)) {
            throw new RuntimeException("Чат с таким названием уже существует");
        }
        GroupChat chat = new GroupChat(groupChatName, LocalDateTime.now(), publicKey);
        groupChatRepository.save(chat);
    }

    // Получить все чаты
    public List<GroupChat> getAllGroupChats() {
        return groupChatRepository.findAll();
    }
}