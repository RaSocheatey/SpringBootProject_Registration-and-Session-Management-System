package com.project5.rcrsms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.project5.rcrsms.Entity.UserEntity;
import com.project5.rcrsms.Repository.UserRepository;
import com.project5.rcrsms.Security.CustomUserDetails;

@Service
public class UserDetailService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not Found"));
        return new CustomUserDetails(userEntity);
    }
}
