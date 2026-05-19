package com.project5.rcrsms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project5.rcrsms.Entity.Conference;
import com.project5.rcrsms.Entity.Session;
import com.project5.rcrsms.Entity.UserEntity;
import com.project5.rcrsms.Repository.ConferenceRepository;
import com.project5.rcrsms.Repository.SessionRepository;
import com.project5.rcrsms.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SessionService {

    @Autowired
    private final SessionRepository sessionRepository;
    @Autowired
    private final ConferenceRepository conferenceRepository;
    @Autowired
    private final UserRepository userRepository;

    public SessionService(SessionRepository sessionRepository,
            ConferenceRepository conferenceRepository,
            UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
    }

    public Session createSession(Session session) {
        validateSession(session);
        return sessionRepository.save(session);
    }
  
    public Session createSessionForConference(Long conferenceId, Session session) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found with id: " + conferenceId));

        session.setConference(conference);
        validateSession(session);
        return sessionRepository.save(session);
    }
  
    @Transactional(readOnly = true)
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    // --- NEW: Get Only Approved Sessions (For Catalog) ---
    @Transactional(readOnly = true)
    public List<Session> getApprovedSessions() {
        return sessionRepository.findByStatus(Session.SessionStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public Optional<Session> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }
   
    @Transactional(readOnly = true)
    public List<Session> getSessionsByConference(Conference conference) {
        return sessionRepository.findByConference(conference);
    }
  
    @Transactional(readOnly = true)
    public List<Session> getSessionsByConferenceId(Long conferenceId) {
        return sessionRepository.findByConferenceConferenceId(conferenceId);
    }
  
    @Transactional(readOnly = true)
    public List<Session> getSessionsByChair(UserEntity chair) {
        return sessionRepository.findByChair(chair);
    }

    @Transactional(readOnly = true)
    public List<Session> getSessionsByChairId(Long chairId) {
        return sessionRepository.findByChair_userId(chairId);
    }

    @Transactional(readOnly = true)
    public List<Session> searchSessionsByTitle(String keyword) {
        return sessionRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<Session> getSessionsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return sessionRepository.findBySessionTimeBetween(startTime, endTime);
    }

    @Transactional(readOnly = true)
    public List<Session> getUpcomingSessions() {
        LocalDateTime now = LocalDateTime.now();
        return sessionRepository.findBySessionTimeGreaterThanEqualOrderBySessionTimeAsc(now);
    }

    @Transactional(readOnly = true)
    public List<Session> getUpcomingSessionsForConference(Long conferenceId) {
        LocalDateTime now = LocalDateTime.now();
        return sessionRepository.findByConferenceConferenceIdAndSessionTimeGreaterThanEqual(conferenceId, now);
    }

    public Session updateSession(Long id, Session sessionDetails) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        session.setTitle(sessionDetails.getTitle());
        session.setSessionTime(sessionDetails.getSessionTime());
        session.setConference(sessionDetails.getConference());
        session.setChair(sessionDetails.getChair());
        session.setProposalAbstract(sessionDetails.getProposalAbstract()); 
        session.setRoom(sessionDetails.getRoom());                         
        session.setStatus(sessionDetails.getStatus());
        
        // Validate AFTER setting ID and properties so we exclude self in conflict check
        validateSession(session);
        return sessionRepository.save(session);
    }

    public Session assignChair(Long sessionId, Long chairId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        UserEntity chair = userRepository.findById(chairId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + chairId));
        session.setChair(chair);
        return sessionRepository.save(session);
    }

    public Session removeChair(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        session.setChair(null);
        return sessionRepository.save(session);
    }

    public void deleteSession(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        sessionRepository.delete(session);
    }

    public void deleteSessionsByConference(Long conferenceId) {
        List<Session> sessions = sessionRepository.findByConferenceConferenceId(conferenceId);
        sessionRepository.deleteAll(sessions);
    }

    @Transactional(readOnly = true)
    public boolean sessionExists(Long id) {
        return sessionRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long countSessions() {
        return sessionRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Session> getApprovedSessionsByConferenceId(Long conferenceId) {
        return sessionRepository.findByConferenceConferenceIdAndStatus(
            conferenceId, 
            com.project5.rcrsms.Entity.Session.SessionStatus.APPROVED
        );
    }

    @Transactional(readOnly = true)
    public long countSessionsByConference(Long conferenceId) {
        return sessionRepository.findByConferenceConferenceId(conferenceId).size();
    }

    @Transactional(readOnly = true)
    public long countSessionsByChair(Long chairId) {
        return sessionRepository.findByChair_userId(chairId).size();
    }

    // --- UPDATED VALIDATION LOGIC ---
    private void validateSession(Session session) {
        if (session.getConference() == null) {
            throw new IllegalArgumentException("Session must be associated with a conference");
        }

        // Date Check
        if (session.getSessionTime() != null && session.getConference() != null) {
            LocalDateTime sessionDateTime = session.getSessionTime();
            LocalDateTime conferenceStart = session.getConference().getStartDate().atStartOfDay();
            LocalDateTime conferenceEnd = session.getConference().getEndDate().atTime(23, 59, 59);

            if (sessionDateTime.isBefore(conferenceStart) || sessionDateTime.isAfter(conferenceEnd)) {
                throw new IllegalArgumentException("Session time must be within conference dates");
            }
        }

        // Room Conflict Check
        if (session.getRoom() != null && session.getSessionTime() != null) {
            boolean isConflict;
            
            if (session.getSessionId() == null) {
                // New Session: Simple check
                isConflict = sessionRepository.existsByRoom_RoomIdAndSessionTime(
                    session.getRoom().getRoomId(), 
                    session.getSessionTime()
                );
            } else {
                // Edit Session: Check excluding SELF
                isConflict = sessionRepository.existsByRoomAndDateAndIdNot(
                    session.getRoom().getRoomId(), 
                    session.getSessionTime(),
                    session.getSessionId()
                );
            }

            if (isConflict) {
                throw new IllegalArgumentException("Room '" + session.getRoom().getName() + "' is already booked at this time!");
            }
        }
    }
}