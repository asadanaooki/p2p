package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderListViewDto {

    private List<PurchaseOrderListRowDto> purchaseOrders;
    
    private List<Integer> pageNumberList;
    
    private int currentPage;

}
