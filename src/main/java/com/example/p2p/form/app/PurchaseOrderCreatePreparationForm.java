package com.example.p2p.form.app;

import java.util.List;

import com.example.p2p.enums.PurchaseOrderType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseOrderCreatePreparationForm {

    private String supplierId;

    private String supplierName;

    @NotNull
    private PurchaseOrderType orderType;

    @NotEmpty
    private List<@Valid PurchaseRequestDetailSelectionRowForm> details;

    @AssertTrue(message = "発注対象の明細を１件以上選択してください。")
    public boolean isAnyDetailSelected() {
        if (orderType == null || details == null || details.isEmpty()) {
            return true;
        }
        return details.stream().anyMatch(d -> Boolean.TRUE.equals(d.getSelected()));
    }

    @AssertTrue(message = "選択数量が不正です。")
    public boolean isSelectedQuantityValid() {
        if (orderType == null || details == null) {
            return true;
        }
        return switch (orderType) {
            case STANDARD -> details.stream()
                .filter(d -> Boolean.TRUE.equals(d.getSelected()))
                .allMatch(d -> d.getSelectedQuantity() != null && d.getSelectedQuantity() >= 1
                        && d.getSelectedQuantity() <= d.getOrderMaxQuantity());

            case SERVICE -> details.stream()
                .filter(d -> Boolean.TRUE.equals(d.getSelected()))
                .allMatch(d -> d.getSelectedQuantity() == null);

        };
    }

    @Data
    public static class PurchaseRequestDetailSelectionRowForm {

        @NotBlank
        private String prDetailId;

        private long orderMaxQuantity;

        private Integer selectedQuantity;

        @NotNull
        private Boolean selected;

    }

}
