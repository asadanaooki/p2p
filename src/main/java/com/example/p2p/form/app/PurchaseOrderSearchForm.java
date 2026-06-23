package com.example.p2p.form.app;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.example.p2p.enums.PurchaseOrderSortBy;
import com.example.p2p.enums.PurchaseOrderStatus;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.util.CommonUtil;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseOrderSearchForm {

    // ページング
    @Positive
    private int page = 1;

    // 内部専用
    private int size = 2; // TODO: 仮値

    // フィルター
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate dueDateFrom;
    
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate dueDateTo;
    
    private String supplierId;

    private PurchaseOrderStatus status;

    // 検索
    @Length(max = 100)
    private String keyword;

    // ソート
    private PurchaseOrderSortBy sortBy = PurchaseOrderSortBy.NUMBER;

    private SortDirection sortDirection = SortDirection.ASC;

    public String getKeywordRegex() {
        return CommonUtil.toRegex(keyword);
    }

    public int getOffset() {
        return CommonUtil.calculateOffset(page, size);
    }

}
