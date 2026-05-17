package com.example.p2p.enums;

import lombok.Getter;

@Getter
public enum DocumentType {

    PR("購買申請"), PO("発注書"), INVOICE("請求書");
    
    private String label;
    
    DocumentType(String label) {
        this.label = label;
    }

}
