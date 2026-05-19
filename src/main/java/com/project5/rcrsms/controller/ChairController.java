package com.project5.rcrsms.controller;

import com.project5.rcrsms.Entity.Session;
import com.project5.rcrsms.Entity.UserEntity;
import com.project5.rcrsms.Repository.UserRepository;
import com.project5.rcrsms.Service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chair")
public class ChairController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('CHAIR')") // Only Chairs can enter
    public String dashboard(Model model) {
        
        // 1. Get the currently logged-in username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 2. Find the full User object from the database
        UserEntity currentUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Get ALL sessions, then Filter for ones assigned to THIS Chair
        List<Session> mySessions = sessionService.getAllSessions().stream()
                .filter(s -> s.getChair() != null && s.getChair().getUserId().equals(currentUser.getUserId()))
                .collect(Collectors.toList());

        model.addAttribute("mySessions", mySessions);
        model.addAttribute("currentUser", currentUser);
        
        return "chair/dashboard";
    }
}