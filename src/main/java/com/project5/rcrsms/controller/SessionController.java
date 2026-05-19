package com.project5.rcrsms.controller;

import com.project5.rcrsms.Entity.Session;
import com.project5.rcrsms.Entity.Session.SessionStatus;
import com.project5.rcrsms.Entity.UserEntity;
import com.project5.rcrsms.Repository.ConferenceRepository;
import com.project5.rcrsms.Repository.UserRepository;
import com.project5.rcrsms.Repository.RoomRepository;
import com.project5.rcrsms.Repository.SessionRepository; // Added Repository Import
import com.project5.rcrsms.Service.SessionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sessions")
public class SessionController {

    @Autowired private SessionService sessionService;
    @Autowired private SessionRepository sessionRepository; 
    @Autowired private ConferenceRepository conferenceRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private RoomRepository roomRepo;

    @GetMapping({"", "/", "/list"}) 
    public String listSessions(Model model) {
        // FIX: Fetch only APPROVED sessions that are in the FUTURE
        List<Session> sessions = sessionRepository.findByStatusAndSessionTimeGreaterThanEqualOrderBySessionTimeAsc(
            SessionStatus.APPROVED, 
            LocalDateTime.now()
        );
        model.addAttribute("sessions", sessions);
        return "session/list";
    }

    // --- UPDATED: Allow BOTH Admin and Chair to create ---
    @PreAuthorize("hasAnyRole('ADMIN', 'CHAIR')") 
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("session", new Session());
        setupFormModels(model); 
        return "session/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Session session = sessionService.getSessionById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid session Id:" + id));
        
        model.addAttribute("session", session);
        setupFormModels(model); 
        return "session/create";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHAIR')")
    @PostMapping("/save")
    public String saveSession(@Valid @ModelAttribute("session") Session session, 
                              BindingResult result, 
                              Model model,
                              Principal principal) {
        
        if (result.hasErrors()) {
            setupFormModels(model); 
            return "session/create"; 
        }

        UserEntity currentUser = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isChair = currentUser.getRole().name().equals("CHAIR");

        if (isChair) {
            if (session.getSessionId() == null) {
                session.setStatus(SessionStatus.PENDING);
                session.setChair(currentUser);
            }
        } else {
            if (session.getStatus() == null) {
                session.setStatus(SessionStatus.APPROVED);
            }
        }

        if (session.getSessionTime() == null) {
            session.setSessionTime(LocalDateTime.now().plusDays(1));
        }

        try {
            if (session.getSessionId() != null) {
                sessionService.updateSession(session.getSessionId(), session);
            } else {
                sessionService.createSession(session);
            }
            
            if (isChair) {
                return "redirect:/chair/dashboard?success=Proposal Submitted";
            } else {
                return "redirect:/admin/schedule?success=Session Saved";
            }

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            setupFormModels(model);
            return "session/create";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteSession(@PathVariable("id") Long id) {
        sessionService.deleteSession(id);
        return "redirect:/admin/schedule";
    }

    private void setupFormModels(Model model) {
        model.addAttribute("conferences", conferenceRepo.findAll());
        model.addAttribute("rooms", roomRepo.findAll());

        List<UserEntity> potentialChairs = userRepo.findAll().stream()
            .filter(u -> {
                var role = u.getRole();
                return role != null && (role.name().equals("CHAIR"));
            })
            .collect(Collectors.toList());

        model.addAttribute("chairs", potentialChairs);
    }

    @GetMapping("/view/{id}")
    public String viewSession(@PathVariable Long id, Model model) {
        Session session = sessionService.getSessionById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid session Id:" + id));
                model.addAttribute("currentSession", session);
        return "session/view";
    }
}