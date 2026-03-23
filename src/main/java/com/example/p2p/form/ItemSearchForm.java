package com.example.p2p.form;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.security.web.header.writers.frameoptions.RegExpAllowFromStrategy;
import org.springframework.util.StringUtils;

import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.ItemSortBy;
import com.example.p2p.enums.SortDirection;

import lombok.Data;

@Data
public class ItemSearchForm {

    // フィルター
    private ItemKind kind;

    private Integer priceMIn;

    private Integer priceMax;

    private String supplierId;

    private Boolean status;

    // 検索
    private String keyword;

    // ソート
    private ItemSortBy sortBy;

    private SortDirection sortDirection;

    public List<String> getKeywords() {
        if (StringUtils.hasText(keyword)) {
            return Arrays.asList(keyword.split("[\\s]+"));
        }
        return Collections.emptyList();
    }
    
    public String getKeywordRegex() {
        return String.join("\\", getKeywords());
    }

}
