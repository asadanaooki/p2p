package com.example.p2p.dto.app;

import com.example.p2p.enums.DetailInputType;
import com.example.p2p.enums.ItemKind;

import lombok.Data;

@Data
public class PurchaseRequestEditDetailDto {

    private String prDetailId;
    
    private int lineNo;
    
    private String itemId;
    
    private String itemName;
    
    private ItemKind kind;
    
    private String unitName;
    
    private String supplierName;
    
    private int price;
    
    private int quantity;
    
    private int subtotalExcludingTax;
    
    public DetailInputType getDetailInputType() {
        return itemId != null ? DetailInputType.CATALOG : DetailInputType.FREE; 
    }
}
