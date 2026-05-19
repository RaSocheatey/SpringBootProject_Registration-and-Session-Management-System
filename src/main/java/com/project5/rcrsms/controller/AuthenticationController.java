package com.project5.rcrsms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project5.rcrsms.Security.CustomUserDetails;
import com.project5.rcrsms.dto.LoginRequest;
import com.project5.rcrsms.dto.LoginResponse;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> loginAPI(
            @RequestBody LoginRequest loginRequest) {
        
        try {
            // Authenticate against the real database
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            // Authentication successful
            LoginResponse response = new LoginResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getRole(),
                "Login successful",
                true
            );
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            // Authentication failed
            LoginResponse errorResponse = new LoginResponse("Invalid credentials", false);
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}