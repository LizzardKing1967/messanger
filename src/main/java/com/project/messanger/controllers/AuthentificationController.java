package com.project.messanger.controllers;

import com.project.messanger.dto.AuthenticationRequest;
import com.project.messanger.dto.AuthenticationResponse;
import com.project.messanger.dto.RegisterRequest;
import com.project.messanger.entity.User;
import com.project.messanger.service.AuthenticationService;
import com.project.messanger.service.CustomUserDetailsService;
import com.project.messanger.service.UserService;
import com.project.messanger.utils.JwtUtil;
import com.project.messanger.utils.StaticVariable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthentificationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {

        try {
            authenticate(request.getUsername(), request.getPassword());

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            Optional<User> user = userService.getUserByUsername(request.getUsername());

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Пользователь не найден"));
            }

            String token = jwtTokenUtil.generateToken(userDetails);

            Cookie jwtAuthToken = new Cookie(StaticVariable.SECURE_COOKIE, token);
            jwtAuthToken.setHttpOnly(true);
            jwtAuthToken.setSecure(false);
            jwtAuthToken.setPath("/");
            jwtAuthToken.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(jwtAuthToken);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", user.get().getUsername()
            ));

        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Аккаунт отключен"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неверный логин или пароль"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Ошибка аутентификации"));
        }
    }
    private void authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. Удаляем куки с токеном
            Cookie jwtAuthToken = new Cookie(StaticVariable.SECURE_COOKIE, null);
            jwtAuthToken.setHttpOnly(true);
            jwtAuthToken.setSecure(false);
            jwtAuthToken.setPath("/");
            jwtAuthToken.setMaxAge(0); // Устанавливаем срок жизни 0 для удаления куки
            response.addCookie(jwtAuthToken);

            String token = extractTokenFromRequest(request);
            if (token != null) {
                jwtTokenUtil.addToBlacklist(token);
            }

            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Logout failed: " + e.getMessage()));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // Проверяем куки
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StaticVariable.SECURE_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
