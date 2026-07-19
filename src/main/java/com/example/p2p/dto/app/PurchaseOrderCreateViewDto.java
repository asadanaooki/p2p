package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderCreateViewDto {
    
    private String supplierId;
    
    private String supplierName;
    
    private String purchaser;
    
    private List<Integer> relatedPrNumbers;
    
    private List<PurchaseOrderLineViewDto> lines;
}
