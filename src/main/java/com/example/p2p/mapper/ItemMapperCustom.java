package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.ItemListItemDto;

@Mapper
public interface ItemMapperCustom {

    List<ItemListItemDto> selectItems(int limit, int offset);
}
