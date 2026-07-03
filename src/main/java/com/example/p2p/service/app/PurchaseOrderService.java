package com.example.p2p.service.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.app.PurchaseOrderDetailDto;
import com.example.p2p.dto.app.PurchaseOrderListRowDto;
import com.example.p2p.dto.app.PurchaseOrderListViewDto;
import com.example.p2p.dto.app.PurchaseOrderSupplierSelectionViewDto;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.form.app.PurchaseOrderSearchForm;
import com.example.p2p.form.app.PurchaseOrderSupplierSelectionSearchForm;
import com.example.p2p.mapper.ApprovalTaskMapperCustom;
import com.example.p2p.mapper.PurchaseOrderDetailMapperCustom;
import com.example.p2p.mapper.PurchaseOrderMapper;
import com.example.p2p.mapper.PurchaseOrderMapperCustom;
import com.example.p2p.mapper.SupplierMapperCustom;
import com.example.p2p.security.CustomUserDetails;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PurchaseOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    private PurchaseOrderMapperCustom purchaseOrderMapperCustom;

    private SupplierMapperCustom supplierMapperCustom;

    private PurchaseOrderDetailMapperCustom purchaseOrderDetailMapperCustom;

    private PurchaseOrderMapper purchaseOrderMapper;

    private ApprovalTaskMapperCustom approvalTaskMapperCustom;

    public PurchaseOrderListViewDto searchPurchaseOrders(PurchaseOrderSearchForm form, CustomUserDetails loginUser) {
        logger.debug("発注一覧取得開始");

        int page = form.getPage();
        PurchaseOrderListViewDto dto = new PurchaseOrderListViewDto();
        List<PurchaseOrderListRowDto> pos = purchaseOrderMapperCustom.selectPurchaseOrders(form,
                loginUser.getPoViewScope(), loginUser.getUsername());
        dto.setPurchaseOrders(pos);
        dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());
        dto.setCurrentPage(page);
        dto.setPageNumberList(CommonUtil.createPageNumbers(purchaseOrderMapperCustom.countPurchaseOrders(form,
                loginUser.getPoViewScope(), loginUser.getUsername()), form.getSize(), page, 2));

        logger.debug("発注一覧取得完了");
        return dto;
    }

    public PurchaseOrderDetailDto getPurchaseOrderDetail(String poId, CustomUserDetails loginUser) {
        logger.debug("発注詳細取得開始");

        PurchaseOrderDetailDto dto = purchaseOrderMapperCustom.selectPurchaseOrderDetailHeader(poId,
                loginUser.getPoViewScope(), loginUser.getUsername());
        dto.setDetails(purchaseOrderDetailMapperCustom.selectPurchaseOrderDetailLines(poId, loginUser.getPoViewScope(),
                loginUser.getUsername()));
        dto.setApprovalProgressSteps(approvalTaskMapperCustom.selectApprovalProgressSteps(poId));
        dto.setCurrentStepOrder(purchaseOrderMapper.selectByPrimaryKey(poId).getCurrentStepOrder());

        logger.debug("発注詳細取得完了");

        return dto;
    }

    public PurchaseOrderSupplierSelectionViewDto searchSupplierSelections(PurchaseOrderType orderType,
            PurchaseOrderSupplierSelectionSearchForm form) {
        ItemKind kind = orderType == PurchaseOrderType.STANDARD ? ItemKind.GOODS : ItemKind.SERVICE;

        PurchaseOrderSupplierSelectionViewDto dto = new PurchaseOrderSupplierSelectionViewDto();
        dto.setSupplierSelections(purchaseOrderMapperCustom.selectPurchaseOrderSupplierSelections(kind, form));
        dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());

        return dto;

    }

}
