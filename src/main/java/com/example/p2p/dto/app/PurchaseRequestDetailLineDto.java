package com.example.p2p.dto.app;

import com.example.p2p.enums.ItemKind;

import lombok.Data;

@Data
public class PurchaseRequestDetailLineDto {

    private String itemName;

    private ItemKind kind;

    private String unitName;

    private String supplierName;

    private Integer quantity;

    private Integer unitPrice;

    private Integer subtotalExcludingTax;

}