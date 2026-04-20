package com.example.p2p.dto.admin;

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

}
