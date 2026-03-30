package com.example.p2p.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemKind {
    GOODS("物品"),
    SERVICE("サービス");
    
    private final String label;
}
