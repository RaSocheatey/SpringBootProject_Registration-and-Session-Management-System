package com.project5.rcrsms.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat; // Import this
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Column(name = "proposal_abstract", columnDefinition = "TEXT")
    private String proposalAbstract;

    public enum SessionStatus {
        PENDING, APPROVED, REJECTED
    }

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @NotNull(message = "Session time is required")
    @Future(message = "Session time must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // Crucial for HTML input
    @Column(name = "session_time")
    private LocalDateTime sessionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chair_id", nullable = true)
    private UserEntity chair;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id", nullable = false)
    @NotNull(message = "Conference is required")
    private Conference conference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = true)
    private Room room;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    public Session() {}

    // Getters and Setters
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getProposalAbstract() { return proposalAbstract; }
    public void setProposalAbstract(String proposalAbstract) { this.proposalAbstract = proposalAbstract; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public LocalDateTime getSessionTime() { return sessionTime; }
    public void setSessionTime(LocalDateTime sessionTime) { this.sessionTime = sessionTime; }

    public UserEntity getChair() { return chair; }
    public void setChair(UserEntity chair) { this.chair = chair; }

    public Conference getConference() { return conference; }
    public void setConference(Conference conference) { this.conference = conference; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public List<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(List<Registration> registrations) { this.registrations = registrations; }
}