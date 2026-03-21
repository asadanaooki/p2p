package com.example.p2p.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.SupplierDetailDto;
import com.example.p2p.entity.Supplier;

@Mapper
public interface SupplierMapperCustom {

    SupplierDetailDto selectSupplierDetail(String supplierId);
    
    int update(Supplier entity);
}
