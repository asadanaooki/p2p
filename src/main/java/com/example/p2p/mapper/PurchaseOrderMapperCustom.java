package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.p2p.dto.app.PurchaseOrderCreateSourceDto;
import com.example.p2p.dto.app.PurchaseOrderDetailDto;
import com.example.p2p.dto.app.PurchaseOrderDetailSelectionViewDto;
import com.example.p2p.dto.app.PurchaseOrderListRowDto;
import com.example.p2p.dto.app.PurchaseOrderSupplierSelectionRowDto;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.app.PurchaseOrderSearchForm;
import com.example.p2p.form.app.PurchaseOrderSupplierSelectionSearchForm;

@Mapper
public interface PurchaseOrderMapperCustom {

    List<PurchaseOrderListRowDto> selectPurchaseOrders(@Param("form") PurchaseOrderSearchForm form,
            @Param("poViewScope") VisibilityScope scope, @Param("userId") String userId);

    int countPurchaseOrders(@Param("form") PurchaseOrderSearchForm form, @Param("poViewScope") VisibilityScope scope,
            @Param("userId") String userId);

    PurchaseOrderDetailDto selectPurchaseOrderDetailHeader(@Param("poId") String poId,
            @Param("poViewScope") VisibilityScope scope, @Param("userId") String userId);

    List<PurchaseOrderSupplierSelectionRowDto> selectPurchaseOrderSupplierSelections(
            @Param("itemKind") ItemKind itemKind, @Param("form") PurchaseOrderSupplierSelectionSearchForm form);

    PurchaseOrderDetailSelectionViewDto selectPurchaseOrderDetailSelectionView(@Param("supplierId") String supplierId,
            @Param("supplierName") String supplierName, @Param("itemKind") ItemKind itemKind);

    PurchaseOrderCreateSourceDto selectPurchaseOrderCreateSource(@Param("supplierId") String supplierId,
            @Param("supplierName") String supplierName, @Param("userId") String userId,
            @Param("prDetailIds") List<String> prDetailIds);

}
