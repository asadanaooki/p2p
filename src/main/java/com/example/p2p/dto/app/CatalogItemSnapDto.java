package com.example.p2p.dto.app;

import com.example.p2p.enums.ItemKind;

import lombok.Data;

@Data
public class CatalogItemSnapDto {
    
    private ItemKind kind;
    
    private String itemName;
    
    private String unitId;
    
    private String unitName;
    
    private String supplierId;
    
    private String supplierName;
    
    private int price;
}
