package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderSupplierSelectionRowDto {

    private String supplierId;

    private String supplierName;

    private Integer detailCount;

    private List<Integer> prNumbers;

    private int totalAmountExcludingTax;

}
