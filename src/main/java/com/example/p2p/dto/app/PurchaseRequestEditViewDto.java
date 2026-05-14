package com.example.p2p.dto.app;

import java.time.LocalDate;
import java.util.List;

import com.example.p2p.dto.admin.SupplierOptionDto;
import com.example.p2p.dto.admin.UnitOptionDto;
import com.example.p2p.enums.PurchaseRequestStatus;

import lombok.Data;

@Data
public class PurchaseRequestEditViewDto {
    // Mybatis用
    private String prId;
    
    // ヘッダー
    private PurchaseRequestStatus status;
    
    private int displayNumber;

    private String requester;
    
    private LocalDate dueDate;
    
    private LocalDate createdAt;
    
    // 明細
    private List<UnitOptionDto> unitOptions;
    
    private List<SupplierOptionDto> supplierOptions;
    
    private List<PurchaseRequestEditDetailDto> details;
    
    // フッター
    private int totalAmountExcludingTax;
    
    private String note;
    
    public int getTaxAmount() {
        return totalAmountExcludingTax * 10 / 100;
    }
    
    public int totalAmountIncludingTax() {
        return totalAmountExcludingTax + getTaxAmount();
    }

}
