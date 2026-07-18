package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderCreateSourceDto {

    // Mybatis用
    private String prDetailId;

    private String supplierName;

    private String purchaser;

    private List<Integer> relatedPrs;

    private List<PurchaseOrderCreateSourceRowDto> details;

}
