package com.maavooripachadi.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    private String identifier;

    @NotBlank
    @Size(min = 6, max = 120)
    private String password;

    @Size(max = 120)
    private String fullName;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public void setEmail(String email) { this.identifier = email; }
    public void setUsername(String username) { this.identifier = username; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
