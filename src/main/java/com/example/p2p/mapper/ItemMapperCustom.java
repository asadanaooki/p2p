package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.admin.ItemDetailDto;
import com.example.p2p.dto.admin.ItemListItemDto;
import com.example.p2p.dto.app.CatalogItemSnapDto;
import com.example.p2p.dto.app.CatalogListRowDto;
import com.example.p2p.form.admin.ItemSearchForm;
import com.example.p2p.form.app.CatalogSearchForm;

@Mapper
public interface ItemMapperCustom {

    List<ItemListItemDto> selectItems(ItemSearchForm form);
    
    int countItems(ItemSearchForm form);
    
    ItemDetailDto selectItemDetail(String itemId);
    
    List<CatalogListRowDto> selectCatalogItems(CatalogSearchForm form);
    
    int countCatalogItems(CatalogSearchForm form);
    
    CatalogItemSnapDto selectCatalogItemSnap(String itemId);
}
