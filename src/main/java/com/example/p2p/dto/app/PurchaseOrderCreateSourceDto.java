package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderCreateSourceDto {

    // Mybatis用
    private String mappingKey;

    private String supplierName;

    private String purchaser;
    
    private String paymentTermName;

    private List<Integer> relatedPrNumbers;

    private List<PurchaseOrderCreateSourceRowDto> details;

}
