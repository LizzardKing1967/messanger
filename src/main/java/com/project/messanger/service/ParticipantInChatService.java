package com.project.messanger.service;

import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import com.project.messanger.repository.ParticipantInChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipantInChatService {

    private final ParticipantInChatRepository participantInChatRepository;

    @Autowired
    public ParticipantInChatService(ParticipantInChatRepository participantInChatRepository) {
        this.participantInChatRepository = participantInChatRepository;
    }

    // Добавить пользователя в чат
    public void addUserToChat(String groupChatName, String role, String username, String publicKey, int status) {
        ParticipantInChatId id = new ParticipantInChatId();
        id.setGroupChatName(groupChatName);
        id.setUsername(username);
        id.setRole_name(role);


        ParticipantInChat participant = new ParticipantInChat(
                id,
                LocalDateTime.now(), // creationDate
                publicKey,
                LocalDateTime.now(), // joinDate
                status
        );

        participantInChatRepository.save(participant);
    }

    // Удалить пользователя из чата
    public void removeUserFromChat(String groupChatName, String username) {
        ParticipantInChatId id = new ParticipantInChatId();
        id.setGroupChatName(groupChatName);
        id.setUsername(username);

        participantInChatRepository.deleteById(id);
    }

    // Получить всех участников чата
    public List<ParticipantInChat> getAllParticipantsInChat(String groupChatName) {
        return participantInChatRepository.findById_GroupChatName(groupChatName);
    }

    // Получить всех участников с ролью "user"
    public List<ParticipantInChat> getUsersWithRoleUser() {
        return participantInChatRepository.findByRole("user"); // Предположим, что статус 0 соответствует роли "user"
    }

    // Поиск участников по нику
    public List<ParticipantInChat> searchParticipantsByUsername(String username) {
        return participantInChatRepository.findByUsernameContaining(username);
    }
}