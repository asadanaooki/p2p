package com.example.p2p.dto.app;

import java.util.List;

import com.example.p2p.dto.admin.SupplierOptionDto;

import lombok.Data;

@Data
public class PurchaseOrderListViewDto {

    private List<PurchaseOrderListRowDto> purchaseOrders;
    
    private List<SupplierOptionDto> supplierOptions;
    
    private List<Integer> pageNumberList;
    
    private int currentPage;

}
