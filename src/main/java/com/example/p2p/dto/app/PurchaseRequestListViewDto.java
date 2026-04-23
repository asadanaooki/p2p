package com.example.p2p.dto.app;

import java.util.List;

import com.example.p2p.dto.admin.SupplierOptionDto;

import lombok.Data;

@Data
public class PurchaseRequestListViewDto {

    private List<PurchaseRequestListRowDto> purchaseRequests;
    
    private List<Integer> pageNumberList;
    
    private int currentPage;
    
    private List<SupplierOptionDto> supplierOptions;

}
