package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.p2p.dto.app.PurchaseOrderDetailLineDto;
import com.example.p2p.enums.VisibilityScope;

@Mapper
public interface PurchaseOrderDetailMapperCustom {

    List<PurchaseOrderDetailLineDto> selectPurchaseOrderDetailLines(@Param("poId") String poId,
            @Param("poViewScope") VisibilityScope scope, @Param("userId") String userId);

//    void bulkInsert(List<PurchaseRequestDetail> details);
//    
//
//    void bulkUpsert(List<PurchaseRequestDetail> details);
}
