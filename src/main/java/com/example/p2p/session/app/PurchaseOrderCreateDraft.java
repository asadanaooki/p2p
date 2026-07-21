package com.example.p2p.session.app;

import java.util.List;

import com.example.p2p.enums.PurchaseOrderType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderCreateDraft {

    private String supplierId;

    private String supplierName;

    private PurchaseOrderType orderType;

    private List<SelectedPurchaseRequestDetail> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedPurchaseRequestDetail {

        private String prDetailId;

        private Integer selectedQuantity;

    }

}
