package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.PurchaseRequestEditViewDto;
import com.example.p2p.dto.app.PurchaseRequestListRowDto;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.app.PurchaseRequestSearchForm;

@Mapper
public interface PurchaseRequestMapperCustom {

    List<PurchaseRequestListRowDto> selectPurchaseRequests(@Param("form") PurchaseRequestSearchForm form,
            @Param("prViewScope") VisibilityScope scope, @Param("userId") String userId);

    int countPurchaseRequests(@Param("form") PurchaseRequestSearchForm form,
            @Param("prViewScope") VisibilityScope scope, @Param("userId") String userId);

    PurchaseRequestDetailDto selectPurchaseRequestDetailHeader(@Param("prId") String prId,
            @Param("prViewScope") VisibilityScope scope, @Param("userId") String userId);
    
    PurchaseRequestEditViewDto selectPurchaseRequestEditView(String prId);
    
    int updateForEdit(PurchaseRequest pr);

}
