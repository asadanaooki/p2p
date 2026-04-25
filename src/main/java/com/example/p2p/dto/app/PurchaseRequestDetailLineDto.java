package com.example.p2p.dto.app;

import lombok.Data;

@Data
public class PurchaseRequestDetailLineDto {

    private String itemName;

    private String kind;

    private String unitName;

    private String supplierName;

    private Integer unitPrice;

    private Integer quantity;

    private Integer subtotal;
}