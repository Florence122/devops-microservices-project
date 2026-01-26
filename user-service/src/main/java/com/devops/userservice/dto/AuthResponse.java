package com.devops.userservice.dto;

import lombok.Data;
import java.util.Set;

@Data
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}
