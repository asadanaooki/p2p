package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.enums.SortDirection;
import com.example.p2p.enums.UserSortBy;
import com.example.p2p.util.CommonUtil;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserSearchForm {

    // ページング
    @Positive
    private int page = 1;

    // 内部専用
    private int size = 2; // TODO: 仮値

    // フィルター
    @Length(min = 36, max = 36)
    private String roleId;

    private Boolean status;

    // 検索
    @Length(max = 100)
    private String keyword;

    // ソート
    private UserSortBy sortBy = UserSortBy.NAME;

    private SortDirection sortDirection = SortDirection.ASC;

    public String getKeywordRegex() {
        return CommonUtil.toRegex(keyword);
    }

    public int getOffset() {
        return CommonUtil.calculateOffset(page, size);
    }

}
