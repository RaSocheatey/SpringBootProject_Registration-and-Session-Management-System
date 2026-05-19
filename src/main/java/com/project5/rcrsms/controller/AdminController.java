package com.project5.rcrsms.controller;

import com.project5.rcrsms.Entity.Session;
import com.project5.rcrsms.Entity.Session.SessionStatus;
import com.project5.rcrsms.Repository.ConferenceRepository;
import com.project5.rcrsms.Repository.RoomRepository;
import com.project5.rcrsms.Repository.SessionRepository;
import com.project5.rcrsms.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") 
public class AdminController {

    @Autowired private SessionRepository sessionRepo;
    @Autowired private ConferenceRepository conferenceRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private UserRepository userRepo;

    // --- 1. DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Session> allSessions = sessionRepo.findAll();

        long totalSessions = allSessions.size();
        long totalConferences = conferenceRepo.count();
        long totalUsers = userRepo.count();
        
        long pendingCount = allSessions.stream()
                .filter(s -> s.getStatus() == SessionStatus.PENDING)
                .count();

        long upcomingCount = allSessions.stream()
                .filter(s -> s.getSessionTime() != null && 
                             s.getSessionTime().isAfter(java.time.LocalDateTime.now()))
                .count();

        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("totalConferences", totalConferences);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("upcomingSessions", upcomingCount); 
        
        List<Session> recentSessions = allSessions.stream()
            .sorted((s1, s2) -> s2.getSessionId().compareTo(s1.getSessionId()))
            .limit(10)
            .collect(Collectors.toList());

        model.addAttribute("recentSessions", recentSessions);
        return "admin/dashboard";
    }

    // --- 2. MANAGE SCHEDULE (FIXED SEARCH LOGIC) ---
    @GetMapping("/schedule")
    public String schedule(Model model, @RequestParam(name = "keyword", required = false) String keyword) {
        List<Session> sessions;

        // Check if keyword is present
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Use the Search method you added to Repository
            sessions = sessionRepo.findByTitleContainingIgnoreCase(keyword);
        } else {
            // Otherwise show all
            sessions = sessionRepo.findAll();
        }

        model.addAttribute("sessions", sessions);
        model.addAttribute("keyword", keyword); // Pass keyword back so input stays filled
        return "admin/schedule"; 
    }

    // --- 3. MANAGE USERS ---
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            ra.addFlashAttribute("successMessage", "User deleted successfully.");
        } else {
            ra.addFlashAttribute("errorMessage", "User not found.");
        }
        return "redirect:/admin/users";
    }

    // --- 4. SAVE SESSION ---
    @PostMapping("/sessions/save")
    public String saveSession(@ModelAttribute Session session) {
        session.setStatus(SessionStatus.APPROVED);
        sessionRepo.save(session);
        return "redirect:/admin/dashboard?success=Session Created";
    }
}