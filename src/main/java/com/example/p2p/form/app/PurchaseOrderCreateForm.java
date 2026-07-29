package com.example.p2p.form.app;

import java.time.LocalDate;
import java.util.List;

import com.example.p2p.enums.PurchaseOrderType;

import lombok.Data;

@Data
public class PurchaseOrderCreateForm {

    // 物品発注・サービス発注の判定に使用する。
    // hiddenで送信する。
    private PurchaseOrderType orderType;

    // 直接作成かPRからの作成かを判定する。
    // hiddenで送信する。
    private boolean directCreate;

    // 物品発注時に使用する。
    private LocalDate deliveryDate;

    // サービス発注時に使用する。
    private LocalDate serviceStartDate;

    // サービス発注時に使用する。
    private LocalDate serviceEndDate;

    // 直接作成時のみ使用する。
    // 既存仕入先を選択した場合に設定される。
    // 仕入先名を自由入力した場合はnull。
    private String supplierId;

    // 直接作成時のみ使用する。
    // 既存仕入先を選択した場合も、
    // 自由入力した場合も名称を設定する。
    private String supplierName;

    // 支払条件をコンボから選択する場合に使用する。
    private String paymentTermId;

    // 備考。
    private String remarks;

    private List<PurchaseOrderLineForm> lines;
}
