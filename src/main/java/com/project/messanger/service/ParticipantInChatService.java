package com.project.messanger.service;

import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import com.project.messanger.repository.ParticipantInChatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ParticipantInChatService {
    private final ParticipantInChatRepository participantInChatRepository;

    public ParticipantInChatService(ParticipantInChatRepository participantInChatRepository) {
        this.participantInChatRepository = participantInChatRepository;
    }

    public void addUserToChat(String groupChatName, String role, String username, String publicKey, int status) {
        ParticipantInChatId id = new ParticipantInChatId();
        id.setGroupChatName(groupChatName);
        id.setUsername(username);
        id.setRole_name(role);

        ParticipantInChat participant = new ParticipantInChat(
                id,
                LocalDate.now(),
                publicKey,
                LocalDate.now(),
                status
        );
        participantInChatRepository.save(participant);
    }

    public void removeUserFromChat(String groupChatName, String username) {
        ParticipantInChatId id = new ParticipantInChatId();
        id.setGroupChatName(groupChatName);
        id.setUsername(username);
        participantInChatRepository.deleteById(id);
    }

    public List<ParticipantInChat> getAllParticipantsInChat(String groupChatName) {
        return participantInChatRepository.findById_GroupChatName(groupChatName);
    }

    public List<ParticipantInChat> getUsersWithRoleUser() {
        return participantInChatRepository.findByRole("user");
    }

    public List<ParticipantInChat> searchParticipantsByUsername(String username) {
        return participantInChatRepository.findByUsernameContaining(username);
    }

    public List<ParticipantInChat> getParticipantsByRole(String groupChatName, String role) {
        return participantInChatRepository.findByGroupChatNameAndRole(groupChatName, role);
    }

    public List<ParticipantInChat> searchParticipants(String username, String role, String groupChat) {
        return participantInChatRepository.findByDynamicQuery(username, role, groupChat);
    }
}