package com.example.p2p.dto.app;

import java.util.List;

import com.example.p2p.dto.admin.SupplierOptionDto;

import lombok.Data;

@Data
public class PurchaseOrderSupplierSelectionViewDto {

    private List<PurchaseOrderSupplierSelectionRowDto> supplierSelections;

    private List<SupplierOptionDto> supplierOptions;

}
