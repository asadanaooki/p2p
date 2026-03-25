package com.example.p2p.form;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

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

    public List<String> getKeywords() {
        if (StringUtils.hasText(keyword)) {
            return Arrays.asList(keyword.split("[\\p{Zs}]+"));
        }
        return Collections.emptyList();
    }
    
    public String getKeywordRegex() {
        String[] regexEscapeTargets = { "(", ")", ".", "+", "/", "-", "[", "]" };
        List<String> keywords = getKeywords();
        keywords.forEach(kw -> {
            for (String es : regexEscapeTargets) {
                kw.replace(es, "\\" + es);
            }
        });
        List<String> escapedKeywords = getKeywords().stream().map(kw -> {
            for (String es : regexEscapeTargets) {
                kw = kw.replace(es, "\\" + es);
            }
            return kw;
        }).toList();

        return "^(?=.*" + String.join(".*)(?=.*", escapedKeywords) + ".*).*$";
    }
    
    public int getOffset() {
       return CommonUtil.calculateOffset(page, size);
    }

}
