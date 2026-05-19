package com.project5.rcrsms;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordMaker {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Change this to whatever password you want to encrypt
        String rawPassword = "myNewPassword123"; 
        
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("------------------------------------------------");
        System.out.println("Your Encrypted Password is:");
        System.out.println(encodedPassword);
        System.out.println("------------------------------------------------");
    }
}