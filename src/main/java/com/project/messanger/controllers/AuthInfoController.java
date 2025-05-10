package com.project.messanger.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthInfoController {

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUsername(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName());
    }
}