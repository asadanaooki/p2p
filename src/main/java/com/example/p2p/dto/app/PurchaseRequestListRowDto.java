package com.example.p2p.dto.app;

import java.time.LocalDate;

import com.example.p2p.enums.PurchaseRequestStatus;

import lombok.Data;

@Data
public class PurchaseRequestListRowDto {

    private String prId;
    
    private int displayNumber;
    
    private String requester;
    
    private LocalDate dueDate;
    
    private int totalAmountExcludingTax;
    
    private PurchaseRequestStatus status;
}
