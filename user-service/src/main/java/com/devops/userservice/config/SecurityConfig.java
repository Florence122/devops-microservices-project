package com.devops.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    
    // Optionally disable security for certain paths
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/api/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
        );
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("=== Configuring Security ===");
        
        http
            // Disable CSRF for API endpoints
            .csrf(csrf -> {
                csrf.disable();
                log.info("CSRF protection disabled");
            })
            
            // Configure authorization
            .authorizeHttpRequests(auth -> {
                log.info("Setting up authorization rules");
                // Allow all requests to API and Actuator
                auth.requestMatchers("/api/**").permitAll();
                auth.requestMatchers("/actuator/**").permitAll();
                auth.requestMatchers("/swagger-ui/**").permitAll();
                auth.requestMatchers("/v3/api-docs/**").permitAll();
                
                // Require authentication for everything else
                auth.anyRequest().authenticated();
            })
            
            // Disable form login and basic auth popup
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());
        
        log.info("=== Security Configuration Complete ===");
        return http.build();
    }
}