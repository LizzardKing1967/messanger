package com.project.messanger.controllers;

import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.service.ParticipantInChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantInChatController {

    private final ParticipantInChatService participantInChatService;

    @Autowired
    public ParticipantInChatController(ParticipantInChatService participantInChatService) {
        this.participantInChatService = participantInChatService;
    }

    // Добавить пользователя в чат
    @PostMapping("/add")
    public ResponseEntity<String> addUserToChat(
            @RequestParam String groupChatName,
            @RequestParam String username,
            @RequestParam String role,
            @RequestParam String publicKey,
            @RequestParam int status) {
        participantInChatService.addUserToChat(groupChatName, role, username, publicKey, status);
        return ResponseEntity.ok("Пользователь добавлен в чат.");
    }

    // Удалить пользователя из чата
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeUserFromChat(
            @RequestParam String groupChatName,
            @RequestParam String username) {
        participantInChatService.removeUserFromChat(groupChatName, username);
        return ResponseEntity.ok("Пользователь удален из чата.");
    }

    // Получить всех участников чата
    @GetMapping("/all")
    public ResponseEntity<List<ParticipantInChat>> getAllParticipantsInChat(@RequestParam String groupChatName) {
        List<ParticipantInChat> participants = participantInChatService.getAllParticipantsInChat(groupChatName);
        return ResponseEntity.ok(participants);
    }

    // Получить всех участников с ролью "user"
    @GetMapping("/users")
    public ResponseEntity<List<ParticipantInChat>> getUsersWithRoleUser() {
        List<ParticipantInChat> users = participantInChatService.getUsersWithRoleUser();
        return ResponseEntity.ok(users);
    }

    // Поиск участников по нику
    @GetMapping("/search")
    public ResponseEntity<List<ParticipantInChat>> searchParticipantsByUsername(@RequestParam String username) {
        List<ParticipantInChat> participants = participantInChatService.searchParticipantsByUsername(username);
        return ResponseEntity.ok(participants);
    }
}