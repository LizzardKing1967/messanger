package com.project.messanger.controllers;

import com.project.messanger.dto.CreateChatRequest;
import com.project.messanger.dto.GroupChatDTO;
import com.project.messanger.entity.GroupChat;
import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import com.project.messanger.service.GroupChatService;
import com.project.messanger.service.ParticipantInChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chats")
public class GroupChatController {

    private final GroupChatService groupChatService;

    private final ParticipantInChatService participantService;

    @Autowired

    public GroupChatController(GroupChatService groupChatService, ParticipantInChatService participantService) {
        this.groupChatService = groupChatService;
        this.participantService = participantService;
    }

    // Создать групповой чат
    @PostMapping("/create")
    public ResponseEntity<?> createChat(@RequestBody CreateChatRequest request) {
        try {
            String creatorUsername = SecurityContextHolder.getContext()
                    .getAuthentication().getName();

            groupChatService.createGroupChatWithParticipants(request, creatorUsername);

            return ResponseEntity.ok("Чат успешно создан");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка создания чата: " + e.getMessage());
        }
    }


    // Получить все чаты
    @GetMapping("/all")
    public ResponseEntity<List<GroupChat>> getAllGroupChats() {
        try {
            List<GroupChat> chats = groupChatService.getAllGroupChats();
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/myChats")
    public ResponseEntity<List<GroupChatDTO>> getUserChats(Authentication authentication) {
        List<GroupChatDTO> groupChatDTOS = groupChatService.getUserChats(authentication);
        return ResponseEntity.ok(groupChatDTOS);
    }
}
