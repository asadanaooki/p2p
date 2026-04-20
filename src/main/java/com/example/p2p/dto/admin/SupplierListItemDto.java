package com.example.p2p.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierListItemDto {
    
    private String supplierId;

    private String name;
    
    private String email;
    
    private String phoneNumber;
    
    private boolean active;
}
