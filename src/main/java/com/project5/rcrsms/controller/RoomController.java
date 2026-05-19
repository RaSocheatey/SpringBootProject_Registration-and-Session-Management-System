package com.project5.rcrsms.controller;

import com.project5.rcrsms.Entity.Room;
import com.project5.rcrsms.Repository.RoomRepository;
import jakarta.validation.Valid; // Import needed for validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Import needed for error handling
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
public class RoomController {

    @Autowired
    private RoomRepository roomRepo;

    // 1. List all rooms & Show "Add" form
    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomRepo.findAll());
        model.addAttribute("newRoom", new Room()); 
        return "admin/rooms";
    }

    // 2. Handle the "Add Room" form submission (UPDATED)
    @PostMapping("/save")
    public String saveRoom(@Valid @ModelAttribute("newRoom") Room room, 
                           BindingResult result, 
                           Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomRepo.findAll());
            return "admin/rooms"; 
        }

        // If valid, save to DB
        roomRepo.save(room);
        return "redirect:/admin/rooms";
    }

    // 3. Delete a room
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        roomRepo.deleteById(id);
        return "redirect:/admin/rooms";
    }
}