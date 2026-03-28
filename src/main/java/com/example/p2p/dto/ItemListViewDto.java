package com.example.p2p.dto;

import java.util.List;

import lombok.Data;

@Data
public class ItemListViewDto {

    private List<ItemListItemDto> items;
    
    private List<SupplierOptionDto> supplierOptions;

    private List<Integer> pageNumberList;
    
    private int currentPage;

}
