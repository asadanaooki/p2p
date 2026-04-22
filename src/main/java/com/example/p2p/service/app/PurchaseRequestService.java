package com.example.p2p.service.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.app.PurchaseRequestListRowDto;
import com.example.p2p.dto.app.PurchaseRequestListViewDto;
import com.example.p2p.form.app.PurchaseRequestSearchForm;
import com.example.p2p.mapper.PurchaseRequestMapperCustom;
import com.example.p2p.mapper.SupplierMapperCustom;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PurchaseRequestService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseRequestService.class);

    private PurchaseRequestMapperCustom purchaseRequestMapperCustom;

    private SupplierMapperCustom supplierMapperCustom;

    public PurchaseRequestListViewDto searchPurchaseRequests(PurchaseRequestSearchForm form) {
        logger.debug("PR一覧取得開始");
        
        int page = form.getPage();
        PurchaseRequestListViewDto dto = new PurchaseRequestListViewDto();
        List<PurchaseRequestListRowDto> prs = purchaseRequestMapperCustom.selectPurchaseRequests(form);
        dto.setPurchaseRequests(prs);
        dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());
        dto.setCurrentPage(page);
        dto.setPageNumberList(CommonUtil.createPageNumbers(purchaseRequestMapperCustom.countPurchaseRequests(form),
                form.getSize(), page, 2));

        logger.debug("PR一覧取得完了");
        return dto;
    }

}
