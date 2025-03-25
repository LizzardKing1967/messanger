package com.project.messanger.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data

public class User {
    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime dateOfRegistration;
    private String publicKey;
    private int status;

    public User() {}

    public User(String username, String email, String passwordHash,
                LocalDateTime dateOfRegistration, String publicKey, int status) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.dateOfRegistration = dateOfRegistration;
        this.publicKey = publicKey;
        this.status = status;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDateTime dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}