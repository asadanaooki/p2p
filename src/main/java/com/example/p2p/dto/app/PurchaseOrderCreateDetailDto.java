package com.example.p2p.dto.app;

import lombok.Data;

@Data
public class PurchaseOrderCreateDetailDto {

    private String prDetailId;
    
    private String itemId;
    
    private String unitId;
    
    private String itemName;
    
    private String unitName;

    private int unitPrice;
    
    private int purchaseRequestQuantity;
    
    private Integer orderQuantity;
    

}
