package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.p2p.dto.app.PurchaseOrderListRowDto;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.app.PurchaseOrderSearchForm;

@Mapper
public interface PurchaseOrderMapperCustom {

    List<PurchaseOrderListRowDto> selectPurchaseOrders(@Param("form") PurchaseOrderSearchForm form,
            @Param("poViewScope") VisibilityScope scope, @Param("userId") String userId);

    int countPurchaseOrders(@Param("form") PurchaseOrderSearchForm form,
            @Param("poViewScope") VisibilityScope scope, @Param("userId") String userId);

//    PurchaseRequestDetailDto selectPurchaseRequestDetailHeader(@Param("prId") String prId,
//            @Param("poViewScope") VisibilityScope scope, @Param("userId") String userId);
//    
//    PurchaseRequestEditViewDto selectPurchaseRequestEditView(String prId);
//    
//    int updateForEdit(PurchaseRequest pr);

}
