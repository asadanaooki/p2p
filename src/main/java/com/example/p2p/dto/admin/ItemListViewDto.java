package com.example.p2p.dto.admin;

import java.util.List;

import lombok.Data;

@Data
public class ItemListViewDto {

    private List<ItemListItemDto> items;
    
    private List<Integer> pageNumberList;
    
    private int currentPage;

}
