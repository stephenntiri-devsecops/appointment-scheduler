package com.acme.scheduler.appointments.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
	    "http://10.0.0.114:3000"
            // If you serve the HTML from another host/port, add it here
            // Example: "http://192.168.1.10:3000"
        )
        .allowedMethods("POST", "OPTIONS", "GET")
        .allowedHeaders("Content-Type", "Idempotency-Key")
        .maxAge(3600);
  }
}
