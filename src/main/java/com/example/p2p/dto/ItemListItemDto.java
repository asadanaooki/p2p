package com.example.p2p.dto;

import com.example.p2p.enums.ItemKind;

import lombok.Data;

@Data
public class ItemListItemDto {
    
    private String itemId;

    private String name;
    
    private ItemKind kind;
    
    private String unitName;
    
    private Integer price;
    
    private String supplierName;
    
    private boolean active;
}
