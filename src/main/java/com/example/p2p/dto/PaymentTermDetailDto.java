package com.example.p2p.dto;

import com.example.p2p.enums.DueDateType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTermDetailDto {

    private String paymentTermId;

    private String name;
    
    private int days;
    
    private DueDateType dueDateType;
    
    private boolean active;
}
