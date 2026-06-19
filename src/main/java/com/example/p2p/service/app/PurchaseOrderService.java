package com.example.p2p.service.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.app.PurchaseOrderListRowDto;
import com.example.p2p.dto.app.PurchaseOrderListViewDto;
import com.example.p2p.dto.app.PurchaseRequestListViewDto;
import com.example.p2p.form.app.PurchaseOrderSearchForm;
import com.example.p2p.mapper.PurchaseOrderMapperCustom;
import com.example.p2p.security.CustomUserDetails;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PurchaseOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);
    
    private PurchaseOrderMapperCustom purchaseOrderMapperCustom;
    
    public PurchaseRequestListViewDto searchPurchaseOrders(PurchaseOrderSearchForm form,
            CustomUserDetails loginUser) {
        logger.debug("発注一覧取得開始");

        int page = form.getPage();
        PurchaseOrderListViewDto dto = new PurchaseOrderListViewDto();
        List<PurchaseOrderListRowDto> pos = purchaseOrderMapperCustom.selectPurchaseOrders(form,
                loginUser.getPrViewScope(), loginUser.getUsername());
        dto.setPurchaseOrders(pos);
        dto.setCurrentPage(page);
        dto.setPageNumberList(CommonUtil.createPageNumbers(purchaseOrderMapperCustom.countPurchaseOrders(form,
                loginUser.getPrViewScope(), loginUser.getUsername()), form.getSize(), page, 2));

        logger.debug("発注一覧取得完了");
        return dto;
    }
}
