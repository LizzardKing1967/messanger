package com.project.messanger.entity;

import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Getter
@Data

public class User implements UserDetails {
    // Getters and Setters
    private String username;
    private String email;
    private String passwordHash;
    private LocalDate dateOfRegistration;
    private String publicKey;
    private int status;
    private int key_version;
    private String name;
    private String lastName;

    public User() {}

    public User(String username, String email, String passwordHash,
                LocalDate dateOfRegistration, String publicKey,
                int key_version, int status, String name, String lastName) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.dateOfRegistration = dateOfRegistration;
        this.publicKey = publicKey;
        this.key_version = key_version;
        this.status = status;
        this.name = name;
        this.lastName = lastName;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override
    public String getUsername() {
        return this.username;

    }
    @Override
    public String getPassword() {
        return this.passwordHash;

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setDateOfRegistration(LocalDate dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}