package com.project5.rcrsms.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/api/login", "/register", "/login", "/error", "/sessions").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/chair/**").hasRole("CHAIR") 
                .requestMatchers("/registrations/add").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                // --- CUSTOM REDIRECT LOGIC ---
                .successHandler((request, response, authentication) -> {
                    var roles = authentication.getAuthorities().stream()
                            .map(r -> r.getAuthority()).toList();

                    // 1. Admin -> Admin Dashboard
                    if (roles.contains("ROLE_ADMIN") || roles.contains("ADMIN")) {
                        response.sendRedirect("/admin/dashboard");
                    } 
                    // 2. Chair -> Chair Dashboard 
                    else if (roles.contains("ROLE_CHAIR") || roles.contains("CHAIR")) {
                        response.sendRedirect("/chair/dashboard");
                    } 
                    // 3. Everyone else -> Conference List
                    else {
                        response.sendRedirect("/conferences");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return httpSecurity.build();
    }
}