package com.project.messanger.controllers;

import com.project.messanger.entity.User;
import com.project.messanger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Создать пользователя
    @PostMapping("/create")
    public ResponseEntity<String> createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String publicKey,
            @RequestParam String status) {

        userService.createUser(username, email, password, publicKey, Integer.parseInt(status));
        return ResponseEntity.ok("Пользователь создан.");
    }

    // Получить всех пользователей
    @GetMapping("/all")
    public List<User> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        return userList;
    }
}