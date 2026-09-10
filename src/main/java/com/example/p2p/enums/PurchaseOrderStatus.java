package com.example.p2p.enums;

import lombok.Getter;

@Getter
public enum PurchaseOrderStatus {
    PENDING("承認待ち"), 
    APPROVED("承認済み"),
    REJECTED("否認"),
    CANCELLED("キャンセル"),
    COMPLETED("完了");

    private String label;

    private PurchaseOrderStatus(String label) {
        this.label = label;
    }

}
