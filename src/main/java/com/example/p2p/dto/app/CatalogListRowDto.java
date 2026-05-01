package com.example.p2p.dto.app;

import com.example.p2p.enums.ItemKind;

import lombok.Data;

@Data
public class CatalogListRowDto {
    
    private String itemId;

    private String name;
    
    private ItemKind kind;
    
    private String supplier;
    
    private String unit;
    
    private int price;
}
