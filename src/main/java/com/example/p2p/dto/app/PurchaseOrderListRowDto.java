package com.example.p2p.dto.app;

import java.time.LocalDate;

import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.enums.PurchaseRequestStatus;

import lombok.Data;

@Data
public class PurchaseOrderListRowDto {

    private String prId;
    
    private int displayNumber;
    
    private PurchaseOrderType orderType;
    
    private String purchaser;
    
    private LocalDate dueDate;
    
    private String supplierName;
    
    private int totalAmountExcludingTax;
    
    private PurchaseRequestStatus status;
}
