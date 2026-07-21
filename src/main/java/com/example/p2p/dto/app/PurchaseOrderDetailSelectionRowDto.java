package com.example.p2p.dto.app;

import lombok.Data;

@Data
public class PurchaseOrderDetailSelectionRowDto {

    // 共通
    private String prDetailId;
    
    private Integer detailIndex;

    private String itemName;
    
    private int unitPrice;
    
    private int subtotalExcludingTax;
    
    // Goodsのみ
    private String unitName;
    
    private int quantity;
}
