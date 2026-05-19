package com.project5.rcrsms.controller;

import com.project5.rcrsms.Entity.Conference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.project5.rcrsms.Service.ConferenceService;
import com.project5.rcrsms.Service.SessionService;

import java.util.List;

@Controller
@RequestMapping("/conferences")
public class ConferenceController {

    @Autowired
    private ConferenceService conferenceService;
    @Autowired
    private SessionService sessionService;

    // 1. List All Conferences (With Filters)
    @GetMapping("") 
    public String listConferences(
            @RequestParam(name = "filter", defaultValue = "upcoming") String filter,
            Model model) {

        // This requires the new Service method below
        List<Conference> conferences = conferenceService.getConferencesByFilter(filter);

        model.addAttribute("conferences", conferences);
        model.addAttribute("currentFilter", filter);
        
        return "conference/list";
    }

    // 2. Show Create Form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("conference", new Conference());
        return "conference/create";
    }

    // 3. Save Conference
    @PostMapping("/save")
    public String saveConference(@ModelAttribute("conference") Conference conference, Model model) {
        try {
            conferenceService.createConference(conference);
            return "redirect:/conferences"; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "conference/create";
        } 
    }
    
    // 4. View Conference Details
    @GetMapping("/{id}")
    public String viewConference(@PathVariable Long id, Model model) {
        Conference conference = conferenceService.getConferenceById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid conference Id:" + id));
            
        model.addAttribute("conference", conference);
        
        // --- CRITICAL FIX: Load ONLY Approved sessions ---
        // This hides the "Pending" proposals from the public view
        model.addAttribute("sessions", sessionService.getApprovedSessionsByConferenceId(id));
        
        return "conference/view";
    }
}