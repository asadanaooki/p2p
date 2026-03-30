package com.example.p2p.dto;

import java.util.List;

import com.example.p2p.enums.ItemKind;

import lombok.Data;

@Data
public class ItemEditViewDto {

    // アイテム情報
    private String name;

    private ItemKind kind;

    private String unitId;

    private Integer price;

    private String supplierId;

    private String description;

    private boolean active;

    // 単位一覧
    private List<UnitOptionDto> unitOptions;

    // サプライヤー一覧
    private List<SupplierOptionDto> supplierOptions;

}
