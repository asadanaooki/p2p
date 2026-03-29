package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.UnitOptionDto;

@Mapper
public interface UnitMapperCustom {
    
    List<UnitOptionDto> selectUnitOptions();
}
