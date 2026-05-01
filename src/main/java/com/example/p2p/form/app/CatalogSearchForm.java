package com.example.p2p.form.app;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.enums.ItemKind;
import com.example.p2p.util.CommonUtil;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CatalogSearchForm {

    // ページング
    @Positive
    private int page = 1;

    // 内部専用
    private int size = 2; // TODO: 仮値

    // フィルター
    private ItemKind kind;

    @Length(min = 36, max = 36)
    private String supplierId;

    // 検索
    @Length(max = 100)
    private String keyword;

    public String getKeywordRegex() {
        return CommonUtil.toRegex(keyword);
    }

    public int getOffset() {
        return CommonUtil.calculateOffset(page, size);
    }
}
