package com.example.p2p.dto.app;

import lombok.Data;

@Data
public class PurchaseOrderDetailLineDto {

    // 共通
    private int lineNo;
    
    private String itemName;
    
    private int lineAmountExcludingTax;
    
    // POのみ
    private String unitName;

    private Integer quantity;

    private Integer unitPrice;
    

}