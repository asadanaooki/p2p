package com.example.p2p.session.app;

import java.util.List;

import com.example.p2p.enums.PurchaseOrderType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PurchaseOrderCreateDraft {

    private final String supplierId;

    private final String supplierName;

    private final PurchaseOrderType orderType;

    private final List<SelectedPurchaseRequestDetail> details;

    @Data
    @AllArgsConstructor
    public static class SelectedPurchaseRequestDetail {

        private final String prDetailId;

        private final Integer selectedQuantity;

    }

}
