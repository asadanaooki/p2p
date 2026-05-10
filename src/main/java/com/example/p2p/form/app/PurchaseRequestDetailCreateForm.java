package com.example.p2p.form.app;

import com.example.p2p.enums.DetailInputType;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.validation.ValidPurchaseRequestDetail;

import lombok.Data;

@Data
@ValidPurchaseRequestDetail
public class PurchaseRequestDetailCreateForm {
    
    private DetailInputType detailInputType;

    // カタログ購買のみ使用
    private String itemId;

    // フリー入力のみ使用
    private ItemKind kind;
    
    private String itemName;

    private String supplierId;

    private String supplierName;

    private String unitId;

    private String unitName;

    private Integer price;

    // 共通
    private Integer quantity;

}
