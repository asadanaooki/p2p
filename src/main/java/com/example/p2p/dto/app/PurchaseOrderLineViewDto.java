package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderLineViewDto {

    private String itemId;

    private String unitId;

    private String itemName;

    private String unitName;

    private int unitPrice;

    private List<PrDetailAllocationViewDto> allocations;

    @Data
    public static class PrDetailAllocationViewDto {

        private String prDetailId;

        private int purchaseRequestQuantity;

        private Integer orderQuantity;

    }

}
