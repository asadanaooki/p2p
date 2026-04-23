package com.example.p2p.enums;

import lombok.Getter;

@Getter
public enum PurchaseRequestStatus {
    PENDING("保留中"), 
    APPROVED("承認済み"),
    REJECTED("拒否"),
    CANCELLED("キャンセル"),
    COMPLETED("完了");

    private String label;

    private PurchaseRequestStatus(String label) {
        this.label = label;
    }

}
