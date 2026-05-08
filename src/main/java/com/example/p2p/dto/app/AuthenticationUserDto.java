package com.example.p2p.dto.app;

import lombok.Data;

@Data
public class AuthenticationUserDto {

    private String userId;
    
    private String passwordHash;
    
    private String email;
    
    private String role;
}
