package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.admin.ItemDetailDto;
import com.example.p2p.dto.admin.ItemListItemDto;
import com.example.p2p.form.admin.ItemSearchForm;

@Mapper
public interface ItemMapperCustom {

    List<ItemListItemDto> selectItems(ItemSearchForm form);
    
    int countItems(ItemSearchForm form);
    
    ItemDetailDto selectItemDetail(String itemId);
}
