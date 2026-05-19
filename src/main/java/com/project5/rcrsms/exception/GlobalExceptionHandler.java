package com.project5.rcrsms.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Catch Access Denied (e.g., Participant trying to enter Admin pages)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Security Alert: You do not have permission to access that section.");
        return "redirect:/sessions";
    }

    // Catch Logic Errors (Duplicate Registration, "User not found", etc.)
    @ExceptionHandler(IllegalStateException.class)
    public String handleLogicErrors(IllegalStateException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/sessions";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, Model model) {
        model.addAttribute("error", "Something went wrong: " + ex.getMessage());
        return "error"; 
    }
}