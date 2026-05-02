package com.example.p2p.service.app;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.app.CatalogItemSnapDto;
import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.PurchaseRequestListRowDto;
import com.example.p2p.dto.app.PurchaseRequestListViewDto;
import com.example.p2p.entity.PurchaseRequest;
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

    public void create(String userId, PurchaseRequestCreateForm form) {
        List<PurchaseRequestDetailForm> details = form.getDetails();
        
        details.stream().filter(f -> f.getDetailInputType() == DetailInputType.CATALOG).forEach(f -> {
            CatalogItemSnapDto dto = itemMapperCustom.selectCatalogItemSnap(f.getItemId());
            modelMapper.map(dto, f);
        });

        int totalExcludingTax = details.stream().mapToInt(i -> i.getQuantity() * i.getPrice()).sum();

        // ヘッダー登録
         PurchaseRequest header = new PurchaseRequest();
         header.setRequesterUserId(userId);
         header.setDueDate(form.getDueDate());
         header.setTotalAmountExcludingTax(totalExcludingTax);
         header.setStatus(PurchaseRequestStatus.PENDING.toString());
         header.setNote(form.getNote());
         purchaseRequestMapper.insertSelective(header);
         
        // 明細登録
         

    }

}
