package com.example.p2p.dto.app;

import java.time.LocalDate;
import java.util.List;

import com.example.p2p.enums.PurchaseRequestStatus;

import lombok.Data;

@Data
public class PurchaseRequestDetailDto {

    // ヘッダー
    private Integer displayNumber;

    private String requester;

    private LocalDate dueDate;

    private int totalAmountExcludingTax;

    private PurchaseRequestStatus status;

    private String note;

    private LocalDate createdAt;

    // 明細
    private List<PurchaseRequestDetailLineDto> details;
    
    public int getTaxAmount() {
        return totalAmountExcludingTax * 10 / 100;
    }
    
    public int totalAmountIncludingTax() {
        return totalAmountExcludingTax + getTaxAmount();
    }
}