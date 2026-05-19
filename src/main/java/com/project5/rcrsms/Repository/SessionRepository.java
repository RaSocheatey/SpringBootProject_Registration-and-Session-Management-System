package com.project5.rcrsms.Repository;

import com.project5.rcrsms.Entity.Conference;
import com.project5.rcrsms.Entity.Session;
import com.project5.rcrsms.Entity.Session.SessionStatus;
import com.project5.rcrsms.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    // --- 1. NEW: Find Approved & Future Sessions (For Catalog) ---
    // This looks for sessions where Status is X AND Time is >= Now, sorted by date.
    List<Session> findByStatusAndSessionTimeGreaterThanEqualOrderBySessionTimeAsc(SessionStatus status, LocalDateTime time);

    // --- 2. Find Approved Sessions by Conference ---
    List<Session> findByConferenceConferenceIdAndStatus(Long conferenceId, SessionStatus status);

    // Existing methods...
    List<Session> findByStatus(SessionStatus status);
    
    @Query("SELECT COUNT(s) > 0 FROM Session s WHERE s.room.roomId = :roomId AND s.sessionTime = :time AND s.sessionId != :excludeSessionId")
    boolean existsByRoomAndDateAndIdNot(@Param("roomId") Long roomId, @Param("time") LocalDateTime time, @Param("excludeSessionId") Long excludeSessionId);

    List<Session> findByConference(Conference conference);
    List<Session> findByConferenceConferenceId(Long conferenceId);
    List<Session> findByConferenceConferenceIdAndSessionTimeGreaterThanEqual(Long conferenceId, LocalDateTime sessionTime);
    List<Session> findByChair(UserEntity chair);
    List<Session> findByChair_userId(Long userId);
    List<Session> findByTitleContainingIgnoreCase(String keyword);
    List<Session> findBySessionTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Session> findBySessionTimeGreaterThanEqualOrderBySessionTimeAsc(LocalDateTime time);
    boolean existsByRoom_RoomIdAndSessionTime(Long roomId, LocalDateTime sessionTime);
}