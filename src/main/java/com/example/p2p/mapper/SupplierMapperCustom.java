package com.example.p2p.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.SupplierDetailDto;

@Mapper
public interface SupplierMapperCustom {

    SupplierDetailDto selectSupplierDetail(String supplierId);
}
