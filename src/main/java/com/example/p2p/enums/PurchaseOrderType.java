package com.example.p2p.enums;

import lombok.Getter;

@Getter
public enum PurchaseOrderType {
    
    STANDARD("物品"),
    SERVICE("サービス");
    
    private String label;
    
    private PurchaseOrderType(String label) {
        this.label = label;
    }
}
