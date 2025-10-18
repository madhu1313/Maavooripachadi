package com.maavooripachadi.security.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    @JsonProperty("identifier")
    @JsonAlias({"email", "phone"})
    private String identifier;

    @NotBlank private String password;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public void setEmail(String email) { this.identifier = email; }
    public void setPhone(String phone) { this.identifier = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
