package com.project5.rcrsms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project5.rcrsms.Entity.Conference;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {

    Optional<Conference> findByName(String name);

    List<Conference> findByLocation(String location);

    List<Conference> findByNameContainingIgnoreCase(String keyword);

    List<Conference> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<Conference> findByStartDateAfterOrderByStartDateAsc(LocalDate date);

    List<Conference> findByEndDateBeforeOrderByStartDateDesc(LocalDate date);

    List<Conference> findByStartDateBeforeAndEndDateAfterOrderByStartDateAsc(LocalDate start, LocalDate end);

    
}