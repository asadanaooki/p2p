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
    
    // PO納期
    private LocalDate deliveryDueDate;
    
    // SO納期
    private LocalDate servicePeriodFrom;
    
    private LocalDate servicePeriodTo;
    
    private String supplierName;
    
    private int totalAmountExcludingTax;
    
    private PurchaseOrderStatus status;
}
