package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderSupplierSelectionDto {

    private String supplierId;
    
    private String supplierName;

    private int detailCount;

    private List<Integer> prDisplayNumbers;

    private int totalAmountExcludingTax;

}
