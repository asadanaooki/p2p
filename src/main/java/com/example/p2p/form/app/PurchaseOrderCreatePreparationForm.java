package com.example.p2p.form.app;

import java.util.List;

import com.example.p2p.enums.PurchaseOrderType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseOrderCreatePreparationForm {

    private String supplierId;

    @NotBlank
    private String supplierName;

    @NotNull
    private PurchaseOrderType orderType;

    @NotEmpty
    private List<@Valid SelectedPurchaseRequestDetailForm> details;

    @AssertTrue(message = "選択数量が不正です。")
    public boolean isSelectedQuantityValid() {
        if (orderType == null) {
            return true;
        }
        else if (orderType == PurchaseOrderType.SERVICE) {
            return details.stream().allMatch(d -> d.getSelectedQuantity() == null);
        }
        else {
            return details.stream().allMatch(d -> d.getSelectedQuantity() != null);
        }
    }

    @Data
    public static class SelectedPurchaseRequestDetailForm {

        @NotBlank
        private String prDetailId;

        @Min(value = 1)
        private Integer selectedQuantity;

    }

}
