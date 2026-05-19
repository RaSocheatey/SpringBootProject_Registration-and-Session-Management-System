package com.project5.rcrsms.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
@EnableMethodSecurity
public class TestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Keep disabled for now
            .authorizeHttpRequests(auth -> auth
                // Allow Home, Login, Register, and Static Resources (CSS/JS)
                .requestMatchers("/", "/index.html", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                // Everything else requires login
                .anyRequest().authenticated()
            )
            // Enable standard Form Login (like in your Test config)
            .formLogin(form -> form
                .loginPage("/login") // If you have a custom login page
                .defaultSuccessUrl("/", true) // Go to home after login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}