package com.project5.rcrsms.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "session_id"})
})
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Long registrationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    @NotNull(message = "Session is required")
    private Session session;

    private LocalDateTime registrationDate = LocalDateTime.now();

    // --- NEW FIELD: ATTENDANCE ---
    @Column(nullable = false)
    private boolean attended = false;

    public Registration() {}

    // Getters and Setters
    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    // --- NEW GETTERS/SETTERS ---
    public boolean isAttended() { return attended; }
    public void setAttended(boolean attended) { this.attended = attended; }
}