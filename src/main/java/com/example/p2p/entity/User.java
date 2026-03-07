package com.example.p2p.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class User {

    private String userId;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
