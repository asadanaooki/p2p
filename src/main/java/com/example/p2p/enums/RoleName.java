package com.example.p2p.enums;

import lombok.Getter;

@Getter
public enum RoleName {
    ADMIN("管理者"),
    MANAGER("マネージャー"),
    MEMBER("一般社員");

    private final String label;

    private RoleName(String label) {
        this.label = label;
    }

}
