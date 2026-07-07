package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderDetailSelectionViewDto {
    
    // Mybatisマッピングのため
    private String viewKey;
    
    private List<PurchaseOrderDetailSelectionPrGroupDto> prGroups;
    
    private int totalAmountExcludingTax;
    
}
