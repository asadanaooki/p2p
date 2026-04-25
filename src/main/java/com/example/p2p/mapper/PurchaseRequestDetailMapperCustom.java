package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.app.PurchaseRequestDetailLineDto;

@Mapper
public interface PurchaseRequestDetailMapperCustom {

    List<PurchaseRequestDetailLineDto> selectPurchaseRequestDetailLines(String prId);

}
