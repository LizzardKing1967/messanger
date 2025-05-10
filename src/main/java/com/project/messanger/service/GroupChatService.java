package com.project.messanger.service;

import com.project.messanger.dto.CreateChatRequest;
import com.project.messanger.dto.GroupChatDTO;
import com.project.messanger.entity.GroupChat;
import com.project.messanger.entity.Message;
import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import com.project.messanger.repository.GroupChatRepository;
import com.project.messanger.utils.ListUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class GroupChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ParticipantInChatService participantService;

    private final GroupChatRepository groupChatRepository;
    private final MessageService messageService ;


    public GroupChatService(SimpMessagingTemplate messagingTemplate, ParticipantInChatService participantService, GroupChatRepository groupChatRepository, MessageService messageService)
    {
        this.messagingTemplate = messagingTemplate;
        this.participantService = participantService;
        this.groupChatRepository = groupChatRepository;
        this.messageService = messageService;
    }

    @Transactional
    public void createGroupChat(String groupChatName) {
        if (groupChatRepository.existsByGroupChatName(groupChatName)) {
            throw new RuntimeException("Чат с таким названием уже существует");
        }
        GroupChat chat = new GroupChat(groupChatName, LocalDateTime.now());
        groupChatRepository.save(chat);
    }

    @Transactional
    public void createGroupChatWithParticipants(CreateChatRequest request, String creatorUsername) {
        createGroupChat(request.getChatName());

        GroupChatDTO chatDTO = new GroupChatDTO(
                request.getChatName(),
                true,
                null,
                "Был создан чат",
                LocalDateTime.now(), LocalDateTime.now()
        );

        request.getEncryptedKeys().forEach((username, encryptedKey) -> {
            String role = username.equals(creatorUsername) ? "admin" : "user";
            addParticipantAndNotify(
                    request.getChatName(),
                    username,
                    role,
                    encryptedKey,
                    chatDTO
            );
        });
    }

    private void addParticipantAndNotify(String chatName, String username, String role,
                                         String encryptedKey, GroupChatDTO chatDTO) {
        ParticipantInChat participant = new ParticipantInChat(
                new ParticipantInChatId(chatName, username),
                role,
                encryptedKey,
                LocalDate.now(),
                1, "default"
        );
        participantService.addUserToChat(participant);
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/chats",
                chatDTO
        );
    }
    public List<GroupChatDTO> getUserChats(Authentication authentication) {
        String username = authentication.getName();

        return participantService.getParticipantByName(username).stream()
                .map(participant -> {
                    String chatName = participant.getId().getGroupChatName();
                    Optional<GroupChat> groupChatOpt = groupChatRepository.findByGroupChatName(chatName);
                    if (groupChatOpt.isEmpty()) return null;

                    GroupChat groupChat = groupChatOpt.get();
                    Message lastMessage = ListUtils.getLast(messageService.getChatMessages(chatName));

                    return new GroupChatDTO(groupChat, lastMessage);
                })
                .filter(Objects::nonNull)
                .sorted(
                        Comparator
                                .comparing((GroupChatDTO dto) ->
                                        Optional.ofNullable(dto.getLastMessageDate()).orElse(LocalDateTime.MIN)
                                ).reversed()
                                .thenComparing(dto ->
                                        Optional.ofNullable(dto.getGroupCreationDate()).orElse(LocalDateTime.MIN)
                                ).reversed()
                )
                .toList();
    }


    // Получить все чаты
    public List<GroupChat> getAllGroupChats() {
        return groupChatRepository.findAll();
    }
}