package com.project5.rcrsms.Repository;

import com.project5.rcrsms.Entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findBySession_sessionId(Long sessionId);
    boolean existsByUser_userIdAndSession_sessionId(Long userId, Long sessionId);
    Long countBySession_sessionId(Long sessionId);
    List<Registration> findByUser_userId(Long attendeeId);
}