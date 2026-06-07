package com.example.p2p.dto.app;

import java.time.LocalDate;

import com.example.p2p.enums.ApprovalStatus;

import lombok.Data;

@Data
public class ApprovalProgressRowDto {
    
    private int stepOrder;

    private String stepName;

    private String userName;

    private ApprovalStatus status;

    private String comment;

    private LocalDate actedAt;

}
