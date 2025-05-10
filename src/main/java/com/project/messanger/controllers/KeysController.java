package com.project.messanger.controllers;

import com.project.messanger.service.ParticipantInChatService;
import com.project.messanger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keys")
public class KeysController {

    private final UserService userService;

    private final ParticipantInChatService participantInChatService;

    @GetMapping("/{username}")
    public ResponseEntity<String> getPublicKey(@PathVariable String username) {
        String publicKey = userService.getPublicKeyByUsername(username);
        if (publicKey != null) {
            return ResponseEntity.ok(publicKey);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }
    }

    @GetMapping("/getEncryptedChatKey/{chatName}")
    public ResponseEntity<String> getChatEncryptedAesKey(@PathVariable String chatName, Authentication authentication) {
        String encryptedAesKey = participantInChatService.getChatEncryptedAesKeyForParticipant(authentication.getName(), chatName);
        if (encryptedAesKey != null) {
            return ResponseEntity.ok(encryptedAesKey);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ключ не найден");
        }
    }
}
