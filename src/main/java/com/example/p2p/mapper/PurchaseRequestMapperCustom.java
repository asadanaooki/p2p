package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.PurchaseRequestListRowDto;
import com.example.p2p.form.app.PurchaseRequestSearchForm;

@Mapper
public interface PurchaseRequestMapperCustom {

    List<PurchaseRequestListRowDto> selectPurchaseRequests(PurchaseRequestSearchForm form);
    
    int countPurchaseRequests(PurchaseRequestSearchForm form);
    
    PurchaseRequestDetailDto selectPurchaseRequestDetailHeader(String prId);
}
