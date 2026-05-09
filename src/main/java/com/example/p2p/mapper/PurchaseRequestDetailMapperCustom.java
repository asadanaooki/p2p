package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.p2p.dto.app.PurchaseRequestDetailLineDto;
import com.example.p2p.entity.PurchaseRequestDetail;
import com.example.p2p.enums.VisibilityScope;

@Mapper
public interface PurchaseRequestDetailMapperCustom {

    List<PurchaseRequestDetailLineDto> selectPurchaseRequestDetailLines(@Param("prId") String prId,
            @Param("prViewScope") VisibilityScope scope, @Param("userId") String userId);

    void bulkInsert(List<PurchaseRequestDetail> details);

}
