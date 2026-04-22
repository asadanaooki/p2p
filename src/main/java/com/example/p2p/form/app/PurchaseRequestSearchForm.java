package com.example.p2p.form.app;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.enums.PurchaseRequestSortBy;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.util.CommonUtil;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseRequestSearchForm {

    // ページング
    @Positive
    private int page = 1;

    // 内部専用
    private int size = 2; // TODO: 仮値

    // フィルター
    @Length(min = 36, max = 36)
    private String supplierId;
    
    private LocalDate dueDateFrom;
    
    private LocalDate dueDateTo;

    private PurchaseRequestStatus status;

    // 検索
    @Length(max = 100)
    private String keyword;

    // ソート
    private PurchaseRequestSortBy sortBy = PurchaseRequestSortBy.NUMBER;

    private SortDirection sortDirection = SortDirection.ASC;

    public String getKeywordRegex() {
        return CommonUtil.toRegex(keyword);
    }

    public int getOffset() {
        return CommonUtil.calculateOffset(page, size);
    }

}
