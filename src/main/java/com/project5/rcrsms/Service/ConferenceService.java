package com.project5.rcrsms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project5.rcrsms.Entity.Conference;
import com.project5.rcrsms.Repository.ConferenceRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConferenceService {
    
    private final ConferenceRepository conferenceRepository;

    @Autowired
    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    @Transactional(readOnly = true)
    public List<Conference> getConferencesByFilter(String filter) {
        LocalDate today = LocalDate.now();

        if (filter == null) {
            return conferenceRepository.findByStartDateAfterOrderByStartDateAsc(today);
        }

        switch (filter.toLowerCase()) {
            case "past":
                // FIX: Matches Repository "findByEndDateBeforeOrderByStartDateDesc"
                return conferenceRepository.findByEndDateBeforeOrderByStartDateDesc(today);
                
            case "ongoing":
                // FIX: Matches Repository "findByStartDateBeforeAndEndDateAfter..."
                return conferenceRepository.findByStartDateBeforeAndEndDateAfterOrderByStartDateAsc(today, today);
                
            case "all":
                return conferenceRepository.findAll();
                
            case "upcoming":
            default:
                // FIX: Matches Repository "findByStartDateAfter..."
                return conferenceRepository.findByStartDateAfterOrderByStartDateAsc(today);
        }
    }

    public Conference createConference(Conference conference) {
        validateConferenceDates(conference);
        return conferenceRepository.save(conference);
    }

    @Transactional(readOnly = true)
    public List<Conference> getAllConferences() {
        return conferenceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Conference> getConferenceById(Long id) {
        return conferenceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Conference> getConferenceByName(String name) {
        return conferenceRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Conference> getConferencesByLocation(String location) {
        return conferenceRepository.findByLocation(location);
    }

    @Transactional(readOnly = true)
    public List<Conference> searchConferencesByName(String keyword) {
        return conferenceRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<Conference> getConferencesByDateRange(LocalDate startDate, LocalDate endDate) {
        return conferenceRepository.findByStartDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Conference> getUpcomingConferences() {
        LocalDate today = LocalDate.now();
        // Updated to match the new strict "Future" logic
        return conferenceRepository.findByStartDateAfterOrderByStartDateAsc(today);
    }

    @Transactional(readOnly = true)
    public List<Conference> getPastConferences() {
        LocalDate today = LocalDate.now();
        // Updated to match the new strict "Past" logic
        return conferenceRepository.findByEndDateBeforeOrderByStartDateDesc(today);
    }

    public Conference updateConference(Long id, Conference conferenceDetails) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found with id: " + id));

        conference.setName(conferenceDetails.getName());
        conference.setLocation(conferenceDetails.getLocation());
        conference.setStartDate(conferenceDetails.getStartDate());
        conference.setEndDate(conferenceDetails.getEndDate());

        validateConferenceDates(conference);
        return conferenceRepository.save(conference);
    }

    public void deleteConference(Long id) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found with id: " + id));
        conferenceRepository.delete(conference);
    }

    @Transactional(readOnly = true)
    public boolean conferenceExists(Long id) {
        return conferenceRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public long countConferences() {
        return conferenceRepository.count();
    }

    private void validateConferenceDates(Conference conference) {
        if (conference.getStartDate() != null && conference.getEndDate() != null) {
            if (conference.getEndDate().isBefore(conference.getStartDate())) {
                throw new IllegalArgumentException("End date must be after or equal to start date");
            }
        }
    }
}