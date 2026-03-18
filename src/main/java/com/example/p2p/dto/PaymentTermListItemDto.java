package com.example.p2p.dto;

import com.example.p2p.entity.PaymentTerm;
import com.example.p2p.enums.DueDateType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentTermListItemDto {
    
    private String paymentTermId;

    private String name;
    
    private int days;
    
    private String dueDateType;
    
    private boolean active;
}
