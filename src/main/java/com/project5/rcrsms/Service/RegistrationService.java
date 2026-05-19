package com.project5.rcrsms.Service;

import com.project5.rcrsms.Entity.Registration;
import com.project5.rcrsms.Entity.Session;
import com.project5.rcrsms.Entity.Room;
import com.project5.rcrsms.Entity.UserEntity;
import com.project5.rcrsms.Repository.RegistrationRepository;
import com.project5.rcrsms.Repository.SessionRepository;
import com.project5.rcrsms.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegistrationService {

    @Autowired private RegistrationRepository registrationRepo;
    @Autowired private SessionRepository sessionRepo;
    @Autowired private UserRepository userRepo;

    /**
     * Register a user by Username and Session ID
     * Handles: Duplicates, Capacity, and Time Conflicts
     */
    public void registerUser(String username, Long sessionId) {
        // 1. Fetch Entities
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found ID: " + sessionId));

        // 2. Check Room & Capacity
        Room room = session.getRoom();
        if (room == null) {
             throw new RuntimeException("This session has no assigned room yet.");
        }
        long currentCount = registrationRepo.countBySession_sessionId(sessionId);
        if (currentCount >= room.getCapacity()) {
            throw new RuntimeException("Session is full! (Capacity: " + room.getCapacity() + ")");
        }

        // 3. Check for Duplicates
        if (registrationRepo.existsByUser_userIdAndSession_sessionId(user.getUserId(), sessionId)) {
            throw new RuntimeException("You are already registered for this session.");
        }

        // 4. CRITICAL: Check Time Conflicts
        List<Registration> myRegistrations = registrationRepo.findByUser_userId(user.getUserId());
        for (Registration reg : myRegistrations) {
            Session existing = reg.getSession();
            // Simple check: Does the new session start at the exact same time?
            // (You can expand this to check date ranges if sessions have duration)
            if (existing.getSessionTime().isEqual(session.getSessionTime())) {
                throw new RuntimeException("Time Conflict! You are already attending '" + existing.getTitle() + "' at this time.");
            }
        }

        // 5. Save
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setSession(session);
        registrationRepo.save(registration);
    }

    // --- HELPER METHODS ---

    @Transactional(readOnly = true)
    public List<Registration> getMyRegistrations(String username) {
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return registrationRepo.findByUser_userId(user.getUserId());
    }
    
    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsBySession(Long sessionId) {
        return registrationRepo.findBySession_sessionId(sessionId);
    }

    public void cancelRegistration(String username, Long registrationId) {
        Registration reg = registrationRepo.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (!reg.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: You can only cancel your own registrations.");
        }
        registrationRepo.delete(reg);
    }
    
    @Transactional(readOnly = true)
    public int getAvailableSpots(Long sessionId) {
        Session session = sessionRepo.findById(sessionId).orElseThrow();
        Room room = session.getRoom();
        if (room == null) return 0;
        long count = registrationRepo.countBySession_sessionId(sessionId);
        return Math.max(0, room.getCapacity() - (int) count);
    }

    // --- ADMIN/CHAIR: Force Remove Participant ---
    public void deleteRegistration(Long registrationId) {
        if (!registrationRepo.existsById(registrationId)) {
            throw new RuntimeException("Registration not found.");
        }
        registrationRepo.deleteById(registrationId);
    }

    
}