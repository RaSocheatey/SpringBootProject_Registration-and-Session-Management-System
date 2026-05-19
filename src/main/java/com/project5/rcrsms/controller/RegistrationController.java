package com.project5.rcrsms.controller;

import com.project5.rcrsms.Entity.Registration;
import com.project5.rcrsms.Entity.Session; // Needed for the view
import com.project5.rcrsms.Repository.RegistrationRepository;
import com.project5.rcrsms.Repository.SessionRepository; // Needed to find session
import com.project5.rcrsms.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired private RegistrationService registrationService;
    @Autowired private RegistrationRepository registrationRepo;
    @Autowired private SessionRepository sessionRepo;

    // --- 1. NEW: VIEW ATTENDANCE SHEET PAGE ---
    @PreAuthorize("hasAnyRole('ADMIN', 'CHAIR')")
    @GetMapping("/sheet/{sessionId}")
    public String viewAttendanceSheet(@PathVariable Long sessionId, Model model) {
        Session session = sessionRepo.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Session ID"));
        
        long presentCount = session.getRegistrations().stream()
                .filter(Registration::isAttended)
                .count();

        // FIX: Rename "session" to "currentSession" to avoid conflict with HTTP Session
        model.addAttribute("currentSession", session); 
        model.addAttribute("presentCount", presentCount);
        
        return "chair/attendance"; 
    }

    // --- 2. TOGGLE ATTENDANCE (Updated to redirect back to Sheet) ---
    @PreAuthorize("hasAnyRole('ADMIN', 'CHAIR')")
    @PostMapping("/attendance/{id}")
    public String toggleAttendance(@PathVariable Long id, HttpServletRequest request) {
        Registration reg = registrationRepo.findById(id).orElseThrow();
        reg.setAttended(!reg.isAttended());
        registrationRepo.save(reg);
        
        // Reload the same page so the user can keep clicking
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/chair/dashboard");
    }

    // --- 3. JOIN SESSION  ---
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/join/{sessionId}")
    public String joinSession(@PathVariable Long sessionId, Principal principal, RedirectAttributes ra, HttpServletRequest request) { 
        try {
            registrationService.registerUser(principal.getName(), sessionId);
            ra.addFlashAttribute("successMessage", "Registration successful!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/conferences"); 
    }

    // --- 4. VIEW MY SCHEDULE ---
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-schedule")
    public String viewMySchedule(Model model, Principal principal) {
        List<Registration> myRegs = registrationService.getMyRegistrations(principal.getName());
        model.addAttribute("registrations", myRegs);
        return "participant/my_schedule";
    }

    // --- 5. REMOVE PARTICIPANT ---
    @PreAuthorize("hasAnyRole('ADMIN', 'CHAIR')")
    @PostMapping("/remove/{regId}")
    public String removeParticipant(@PathVariable Long regId, HttpServletRequest request, RedirectAttributes ra) {
        try {
            registrationService.deleteRegistration(regId);
            ra.addFlashAttribute("successMessage", "Participant removed.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
    
    // --- 3. CANCEL REGISTRATION (For Participants) ---
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cancel/{regId}")
    public String cancelRegistration(@PathVariable Long regId, 
                                     Principal principal, 
                                     RedirectAttributes ra) {
        try {
            // This calls your service to ensure only the owner can cancel
            registrationService.cancelRegistration(principal.getName(), regId);
            ra.addFlashAttribute("successMessage", "You have successfully unregistered from the session.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/registrations/my-schedule";
    }
}