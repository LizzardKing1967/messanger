package com.project.messanger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "\"user\"")
public class User {

    public User() {}
    public User(String username, String email, String passwordHash, LocalDateTime dateOfRegistration, String publicKey, int status) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.dateOfRegistration = dateOfRegistration;
        this.publicKey = publicKey;
        this.status = status;
    }

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime dateOfRegistration;

    @Column(nullable = false, unique = true)
    private String publicKey;

    @Column(nullable = false)
    private int status;
}

