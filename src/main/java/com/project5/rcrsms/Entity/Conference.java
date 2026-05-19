package com.project5.rcrsms.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conferences")
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conference_id")
    private Long conferenceId;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    public Conference() {}


    public Long getConferenceId() { return conferenceId; }
    public void setConferenceId(Long conferenceId) { this.conferenceId = conferenceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }
}