package com.project.messanger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // генерирует геттеры, сеттеры, toString, equals, hashCode
@Builder // генерирует static builder()
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
}
