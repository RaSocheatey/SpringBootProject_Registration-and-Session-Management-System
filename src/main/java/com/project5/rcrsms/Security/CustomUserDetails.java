package com.project5.rcrsms.Security;

import com.project5.rcrsms.Entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    // This is the "Point"! We can now access the Database ID
    public Long getId() { 
        return user.getUserId(); 
    }

    public String getRole() { 
        return user.getRole().name(); 
    }

    @Override
    public String getUsername() { return user.getUsername(); }
    
    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Formats the role as "ROLE_ADMIN" or "ROLE_USER" for Spring Security
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}