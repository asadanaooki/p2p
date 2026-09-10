package com.example.p2p.form.app;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderLineForm {

    // 直接作成時のみ使用する。
    // 既存アイテムを選択した場合に設定される。
    // 品名を自由入力した場合はnull。
    private String itemId;

    // 直接作成時のみ使用する。
    // 既存単位を選択した場合に設定される。
    // 単位名を自由入力した場合はnull。
    private String unitId;

    // 直接作成時のみ使用する。
    private String itemName;

    // 直接作成時のみ使用する。
    private String unitName;

    // 直接作成時のみ使用する。
    private Integer unitPrice;

    // 直接作成時の発注数量。
    // PRから作成する場合は、
    // allocationsの数量合計から算出する。
    private Integer quantity;

    // PRから作成する場合に使用する。
    // 同一商品に紐づく複数のPR明細を保持する。
    private List<PurchaseOrderAllocationForm> allocations;
}
