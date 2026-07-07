package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderDetailSelectionPrGroupDto {
    
    private String prId;
    
    private int displayNumber;
    
    private List<PurchaseOrderDetailSelectionRowDto> detailRows;
    
    private int prTotalAmountExcludingTax;
}
