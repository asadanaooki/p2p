package com.example.p2p.dto;

import com.example.p2p.enums.ItemKind;

import lombok.Data;

@Data
public class ItemDetailDto {

    private String name;
    
    private ItemKind kind;
    
    private String unitName;
    
    private Integer price;
    
    private String supplierName;
    
    private String description;
    
    private boolean active;
}
