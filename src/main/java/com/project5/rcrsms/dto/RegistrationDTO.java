package com.project5.rcrsms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long") 
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(PARTICIPANT|CHAIR)$", message = "Invalid role selected")
    private String role;

    // Constructors
    public RegistrationDTO() {
    }

    public RegistrationDTO(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password.trim() : null;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}