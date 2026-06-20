package com.example.p2p.dto.app;

import java.time.LocalDate;

import com.example.p2p.enums.PurchaseOrderStatus;
import com.example.p2p.enums.PurchaseOrderType;

import lombok.Data;

@Data
public class PurchaseOrderListRowDto {

    private String poId;
    
    private int displayNumber;
    
    private PurchaseOrderType orderType;
    
    private String purchaser;
    
    private LocalDate dueDate;
    
    private String supplierName;
    
    private int totalAmountExcludingTax;
    
    private PurchaseOrderStatus status;
}
