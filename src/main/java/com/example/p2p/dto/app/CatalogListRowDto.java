package com.example.p2p.dto.app;

import com.example.p2p.enums.ItemKind;

import lombok.Data;

@Data
public class CatalogListRowDto {
    
    private String itemId;

    private String itemName;
    
    private ItemKind kind;
    
    private String supplierId;
    
    private String supplierName;
    
    private String unitId;
    
    private String unitName;
    
    private int price;
}
