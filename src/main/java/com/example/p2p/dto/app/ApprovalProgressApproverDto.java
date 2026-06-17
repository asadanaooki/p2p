package com.example.p2p.dto.app;

import java.time.LocalDate;

import com.example.p2p.enums.ApprovalStatus;

import lombok.Data;

@Data
public class ApprovalProgressApproverDto {
    
    // Mybatisマッピング用
    private String userId;
    
    private String userName;

    private ApprovalStatus status;

    private String comment;

    private LocalDate actedAt;

}
