package com.acme.scheduler.appointments.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/api/**").permitAll()
            .anyRequest().permitAll()
        );

    // IMPORTANT: do NOT enable oauth2ResourceServer(jwt) in local dev
    return http.build();
  }
}
