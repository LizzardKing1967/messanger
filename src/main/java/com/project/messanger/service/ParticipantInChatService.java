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



    public void addUserToChat(ParticipantInChat participant) {
        ParticipantInChatId id = new ParticipantInChatId();
        participantInChatRepository.save(participant);
    }

    public void removeUserFromChat(String groupChatName, String username) {
        ParticipantInChatId id = new ParticipantInChatId();
        id.setGroupChatName(groupChatName);
        id.setUsername(username);
        participantInChatRepository.deleteById(id);
    }

    public List<ParticipantInChat> getAllParticipantsInChat(String groupChatName) {
        return participantInChatRepository.findByGroupChatName(groupChatName);
    }

    public List<ParticipantInChat> getUsersWithRoleUser() {
        return participantInChatRepository.findByRole("user");
    }


    public List<ParticipantInChat> getParticipantsByRole(String groupChatName, String role) {
        return participantInChatRepository.findByGroupChatNameAndRole(groupChatName, role);
    }

    public List<ParticipantInChat> searchParticipants(String username, String role, String groupChat) {
        return participantInChatRepository.findByDynamicQuery(username, role, groupChat);
    }

    public String getChatEncryptedAesKeyForParticipant(String username, String groupChatname)
    {
        String encryptedKey = participantInChatRepository.findEncryptedAesKeyForParticipant(username, groupChatname);
        return  encryptedKey;
    }

    public List<ParticipantInChat> getParticipantByName(String username)
    {
        return participantInChatRepository.findByUsername(username);
    }
}