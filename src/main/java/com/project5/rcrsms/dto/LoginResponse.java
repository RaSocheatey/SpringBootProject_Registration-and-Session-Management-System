package com.project5.rcrsms.dto;

public class LoginResponse {
    private Long userId;
    private String username;
    private String role;
    private String message;
    private boolean success;

    public LoginResponse() {
    }

    // Constructor for Successful Login
    public LoginResponse(Long userId, String username, String role, String message, boolean success) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.message = message;
        this.success = success;
    }

    // Constructor for Failed Login
    public LoginResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}