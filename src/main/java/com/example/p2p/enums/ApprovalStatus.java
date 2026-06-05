package com.example.p2p.enums;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
    PENDING("承認待ち"),
    APPROVED("承認済み"),
    REJECTED("否認");
    
    private String label;
    
    ApprovalStatus(String label) {
        this.label = label;
    }
}
