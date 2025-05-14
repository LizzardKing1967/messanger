package com.project.messanger.service;

import com.project.messanger.dto.AuthenticationRequest;
import com.project.messanger.dto.AuthenticationResponse;
import com.project.messanger.dto.RegisterRequest;
import com.project.messanger.entity.User;
import com.project.messanger.repository.UserRepository;
import com.project.messanger.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtUtil jwtService;

    @Autowired
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        // Проверка на существующего пользователя
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Сохранение пользователя
        User savedUser = userRepository.save(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getRsaPublicKey(), 1, request.getName(), request.getLastName());

        // Генерация JWT токена
        String jwtToken = jwtService.generateToken(savedUser);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        // Аутентификация пользователя
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Получаем пользователя из репозитория
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        // Генерация JWT токена
        var jwtToken = jwtService.generateToken(user);

        // Создание cookie с токеном
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true); // Токен доступен только серверу
        cookie.setSecure(true); // Использовать только по HTTPS
        cookie.setPath("/"); // Доступен для всех путей
        cookie.setMaxAge(3600); // Время жизни токена - 1 час
        response.addCookie(cookie); // Добавляем cookie в ответ

        // Возвращаем объект с токеном в теле ответа (если нужно для других целей)
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}