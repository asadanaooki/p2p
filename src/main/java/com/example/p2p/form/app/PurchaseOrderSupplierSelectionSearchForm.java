package com.example.p2p.form.app;

import com.example.p2p.enums.PurchaseOrderSupplierSelectionSortBy;
import com.example.p2p.enums.SortDirection;

import lombok.Data;

@Data
public class PurchaseOrderSupplierSelectionSearchForm {

    private String supplierId;

    private PurchaseOrderSupplierSelectionSortBy sortBy = PurchaseOrderSupplierSelectionSortBy.SUPPLIER;

    private SortDirection sortDirection = SortDirection.ASC;

}
