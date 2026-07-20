package com.example.p2p.session.app;

import java.util.List;

import com.example.p2p.enums.PurchaseOrderType;

import lombok.Data;

@Data
public class PurchaseOrderCreateDraft {

    private String supplierId;

    private String supplierName;

    private PurchaseOrderType orderType;

    private List<SelectedPurchaseRequestDetail> details;

    @Data
    public static class SelectedPurchaseRequestDetail {

        private String prDetailId;

        private Integer selectedQuantity;

    }

}
