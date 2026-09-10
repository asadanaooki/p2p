package com.example.p2p.form.app;

import lombok.Data;

@Data
public class PurchaseOrderAllocationForm {

    // 発注元となるPR明細ID。
    private String prDetailId;

    // このPR明細から発注する数量。
    private Integer quantity;
}
