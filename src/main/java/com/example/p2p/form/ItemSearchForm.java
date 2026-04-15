package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.ItemSortBy;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.util.CommonUtil;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ItemSearchForm {

    // ページング
    @Positive
    private int page = 1;

    // 内部専用
    private int size = 2; // TODO: 仮値

    // フィルター
    private ItemKind kind;

    @PositiveOrZero
    private Integer priceMin;

    @PositiveOrZero
    private Integer priceMax;

    @Length(min = 36, max = 36)
    private String supplierId;

    private Boolean status;

    // 検索
    @Length(max = 100)
    private String keyword;

    // ソート
    private ItemSortBy sortBy = ItemSortBy.NAME;

    private SortDirection sortDirection = SortDirection.ASC;

    @AssertTrue(message = "{item.price.range.invalid}")
    public boolean isPriceRangeValid() {
        if (priceMin == null || priceMax == null) {
            return true;
        }
        return priceMin <= priceMax;
    }

    public String getKeywordRegex() {
        return CommonUtil.toRegex(keyword);
    }

    public int getOffset() {
        return CommonUtil.calculateOffset(page, size);
    }

}
