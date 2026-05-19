package com.project5.rcrsms.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project5.rcrsms.Entity.Role;
import com.project5.rcrsms.Entity.UserEntity;
import com.project5.rcrsms.Repository.UserRepository;
import com.project5.rcrsms.dto.RegistrationDTO;

import jakarta.validation.Valid;

@Controller
public class MainController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        // If user is not authenticated, show public landing page
        if (principal == null) {
            model.addAttribute("title", "Welcome to ConfSys");
            return "index";
        }
        
        // Get the authenticated user
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // Redirect based on user role
        switch (user.getRole()) {
            case ADMIN:
                return "redirect:/admin/dashboard";
            case CHAIR:
                return "redirect:/sessions";
            case PARTICIPANT:
                return "redirect:/conferences";
            default:
                model.addAttribute("title", "Welcome to ConfSys");
                return "index";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Sign Up");
        // We add an empty DTO so the form has something to hold the data
        model.addAttribute("user", new RegistrationDTO()); 
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") RegistrationDTO registrationDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // 1. Validation Check (Short password, empty fields, etc.)
        if (result.hasErrors()) {
            model.addAttribute("title", "Sign Up");
            return "auth/register"; // Return to form with error messages displayed
        }

        try {
            // 2. SECURITY CHECK: Prevent public user from registering as ADMIN
            if ("ADMIN".equalsIgnoreCase(registrationDto.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Security Warning: Admin registration is not allowed.");
                return "redirect:/register";
            }   
        
            // 3. Whitelist Check (Double safety)
            // If it's not CHAIR or PARTICIPANT, force it to PARTICIPANT
            String safeRole = registrationDto.getRole();
            if (!"CHAIR".equalsIgnoreCase(safeRole) && !"PARTICIPANT".equalsIgnoreCase(safeRole)) {
                safeRole = "PARTICIPANT";
            }

            // 4. Check if username already exists
            if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/register";
            }
            
            // 5. Create new user with encoded password
            UserEntity newUser = new UserEntity();
            newUser.setUsername(registrationDto.getUsername());
            newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            newUser.setRole(Role.valueOf(safeRole.toUpperCase()));
            
            // 6. Save to database
            userRepository.save(newUser);
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
}