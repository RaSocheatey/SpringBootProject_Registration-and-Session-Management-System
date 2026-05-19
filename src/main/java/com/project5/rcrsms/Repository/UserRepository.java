package com.project5.rcrsms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List; // <--- Import this
import com.project5.rcrsms.Entity.UserEntity;
import com.project5.rcrsms.Entity.Role; // <--- Import your Role enum

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    
    // NEW METHOD: Find all users with a specific role (e.g., all CHAIRs)
    List<UserEntity> findByRole(Role role); 
}