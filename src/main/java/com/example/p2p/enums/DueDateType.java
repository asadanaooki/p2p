package com.example.p2p.enums;

import lombok.Getter;

@Getter
public enum DueDateType {
    NET_DAYS("指定日数後"),
    THIS_MONTH_DAY("当月指定日"),
    NEXT_MONTH_DAY("翌月指定日");
    
   private DueDateType(String label) {
        this.label = label;
    }

    private final String label;
}
