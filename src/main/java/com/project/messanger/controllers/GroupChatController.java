package com.project.messanger.controllers;

import com.project.messanger.entity.GroupChat;
import com.project.messanger.service.GroupChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class GroupChatController {

    private final GroupChatService groupChatService;

    public GroupChatController(GroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    // Создать групповой чат
    @PostMapping("/create")
    public ResponseEntity<String> createGroupChat(@RequestParam String groupChatName, @RequestParam String publicKey) {
        groupChatService.createGroupChat(groupChatName, publicKey);
        return ResponseEntity.ok("Чат создан.");
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
}
