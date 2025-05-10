package com.project.messanger.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 20, message = "Имя пользователя должно быть от 3 до 20 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Имя пользователя может содержать только буквы, цифры и подчеркивание")
    private String username;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\- ]+$", message = "Имя может содержать только буквы и дефис")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\- ]+$", message = "Фамилия может содержать только буквы и дефис")
    private String lastName;

    @NotBlank
    private String rsaPublicKey;
}