package com.example.p2p.service.app;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.CatalogItemSnapDto;
import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.PurchaseRequestListRowDto;
import com.example.p2p.dto.app.PurchaseRequestListViewDto;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.entity.PurchaseRequestDetail;
import com.example.p2p.enums.DetailInputType;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.form.app.PurchaseRequestCreateForm;
import com.example.p2p.form.app.PurchaseRequestDetailForm;
import com.example.p2p.form.app.PurchaseRequestSearchForm;
import com.example.p2p.mapper.ItemMapper;
import com.example.p2p.mapper.ItemMapperCustom;
import com.example.p2p.mapper.PurchaseRequestDetailMapperCustom;
import com.example.p2p.mapper.PurchaseRequestMapper;
import com.example.p2p.mapper.PurchaseRequestMapperCustom;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PurchaseRequestService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseRequestService.class);

    private PurchaseRequestMapper purchaseRequestMapper;

    private PurchaseRequestMapperCustom purchaseRequestMapperCustom;

    private PurchaseRequestDetailMapperCustom purchaseRequestDetailMapperCustom;

    private ItemMapper itemMapper;

    private ItemMapperCustom itemMapperCustom;

    private ModelMapper modelMapper;

    public PurchaseRequestListViewDto searchPurchaseRequests(PurchaseRequestSearchForm form) {
        logger.debug("PR一覧取得開始");

        int page = form.getPage();
        PurchaseRequestListViewDto dto = new PurchaseRequestListViewDto();
        List<PurchaseRequestListRowDto> prs = purchaseRequestMapperCustom.selectPurchaseRequests(form);
        dto.setPurchaseRequests(prs);
        dto.setCurrentPage(page);
        dto.setPageNumberList(CommonUtil.createPageNumbers(purchaseRequestMapperCustom.countPurchaseRequests(form),
                form.getSize(), page, 2));

        logger.debug("PR一覧取得完了");
        return dto;
    }

    public PurchaseRequestDetailDto getPurchaseRequestDetail(String prId) {
        logger.debug("PR詳細取得開始");

        PurchaseRequestDetailDto dto = purchaseRequestMapperCustom.selectPurchaseRequestDetailHeader(prId);
        dto.setDetails(purchaseRequestDetailMapperCustom.selectPurchaseRequestDetailLines(prId));

        logger.debug("PR詳細取得完了");

        return dto;
    }

    @Transactional
    public void create(String userId, PurchaseRequestCreateForm form) {
        String prId = UUID.randomUUID().toString();
        // 明細作成
        List<PurchaseRequestDetail> details = toDetailEntities(prId, form.getDetails());

        int totalExcludingTax = details.stream().mapToInt(i -> i.getSubtotalExcludingTax()).sum();

        // ヘッダー登録
        PurchaseRequest header = new PurchaseRequest();

        header.setPrId(prId);
        header.setRequesterUserId(userId);
        header.setDueDate(form.getDueDate());
        header.setTotalAmountExcludingTax(totalExcludingTax);
        header.setStatus(PurchaseRequestStatus.PENDING.toString());
        header.setNote(form.getNote());
        purchaseRequestMapper.insertSelective(header);

        // 明細登録
        purchaseRequestDetailMapperCustom.bulkInsert(details);

    }

    private List<PurchaseRequestDetail> toDetailEntities(String prId, List<PurchaseRequestDetailForm> details) {
        List<PurchaseRequestDetail> list = new ArrayList<PurchaseRequestDetail>();

        int lineNo = 1;
        for (PurchaseRequestDetailForm form : details) {
            PurchaseRequestDetail prd = new PurchaseRequestDetail();
            if (form.getDetailInputType() == DetailInputType.CATALOG) {
                CatalogItemSnapDto dto = itemMapperCustom.selectCatalogItemSnap(form.getItemId());
                prd.setItemId(form.getItemId());
                prd.setSnapItemName(dto.getItemName());
                prd.setSnapKind(dto.getKind().toString());
                prd.setUnitId(dto.getUnitId());
                prd.setSnapUnitName(dto.getUnitName());
                prd.setSupplierId(dto.getSupplierId());
                prd.setSnapSupplierName(dto.getSupplierName());
                prd.setSnapUnitPrice(dto.getPrice());

            }
            else if (form.getDetailInputType() == DetailInputType.FREE) {
                prd.setItemId(null);
                prd.setSnapItemName(form.getItemName());
                prd.setSnapKind(form.getKind().toString());
                prd.setUnitId(form.getUnitId());
                prd.setSnapUnitName(form.getUnitName());
                prd.setSupplierId(form.getSupplierId());
                prd.setSnapSupplierName(form.getSupplierName());
                prd.setSnapUnitPrice(form.getPrice());

            }
            prd.setPrId(prId);
            prd.setLineNo(lineNo++);
            prd.setQuantity(form.getQuantity());
            prd.setSubtotalExcludingTax(prd.getQuantity() * prd.getSnapUnitPrice());

            list.add(prd);
        }

        return list;
    }

}
