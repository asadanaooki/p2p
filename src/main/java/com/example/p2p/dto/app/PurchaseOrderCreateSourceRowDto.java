package com.example.p2p.dto.app;

import lombok.Data;

@Data
public class PurchaseOrderCreateSourceRowDto {

    private String prDetailId;

    private String itemId;

    private String unitId;

    private String itemName;

    private String unitName;

    private int unitPrice;

    private int purchaseRequestQuantity;

}
