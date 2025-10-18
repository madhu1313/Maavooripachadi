package com.maavooripachadi.security.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class AssignRoleRequest {
    @NotBlank
    @JsonAlias({"email"})
    private String identifier;
    @NotBlank private String role;
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
