package com.example.p2p.form.app;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.enums.PurchaseOrderType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    List<@Valid SelectedPurchaseRequestDetailForm> details;
    
    // TODO:　@assertTrue orderType==StandardならばselectedQuantity必須　バリデーション書く
    
    @Data
    public static class SelectedPurchaseRequestDetailForm {
        @NotBlank
        private String prDetailId;
        
        @Min(value = 1)
        private Integer selectedQuantity;
    }

}
