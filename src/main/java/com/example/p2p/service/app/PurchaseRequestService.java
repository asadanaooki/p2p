package com.example.p2p.service.app;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.CatalogItemSnapDto;
import com.example.p2p.dto.app.PurchaseRequestCreateViewDto;
import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.PurchaseRequestEditViewDto;
import com.example.p2p.dto.app.PurchaseRequestListRowDto;
import com.example.p2p.dto.app.PurchaseRequestListViewDto;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.entity.PurchaseRequestDetail;
import com.example.p2p.enums.DetailInputType;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.form.app.PurchaseRequestCreateForm;
import com.example.p2p.form.app.PurchaseRequestDetailCreateForm;
import com.example.p2p.form.app.PurchaseRequestDetailEditForm;
import com.example.p2p.form.app.PurchaseRequestEditForm;
import com.example.p2p.form.app.PurchaseRequestSearchForm;
import com.example.p2p.mapper.ItemMapperCustom;
import com.example.p2p.mapper.PurchaseRequestDetailMapper;
import com.example.p2p.mapper.PurchaseRequestDetailMapperCustom;
import com.example.p2p.mapper.PurchaseRequestMapper;
import com.example.p2p.mapper.PurchaseRequestMapperCustom;
import com.example.p2p.mapper.SupplierMapperCustom;
import com.example.p2p.mapper.UnitMapperCustom;
import com.example.p2p.mapper.UsersMapperCustom;
import com.example.p2p.security.CustomUserDetails;
import com.example.p2p.util.CommonUtil;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PurchaseRequestService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseRequestService.class);

    private PurchaseRequestMapper purchaseRequestMapper;

    private PurchaseRequestMapperCustom purchaseRequestMapperCustom;

    private PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    private PurchaseRequestDetailMapperCustom purchaseRequestDetailMapperCustom;

    private ItemMapperCustom itemMapperCustom;

    private UnitMapperCustom unitMapperCustom;

    private SupplierMapperCustom supplierMapperCustom;

    private UsersMapperCustom usersMapperCustom;

    public PurchaseRequestListViewDto searchPurchaseRequests(PurchaseRequestSearchForm form,
            CustomUserDetails loginUser) {
        logger.debug("PR一覧取得開始");

        int page = form.getPage();
        PurchaseRequestListViewDto dto = new PurchaseRequestListViewDto();
        List<PurchaseRequestListRowDto> prs = purchaseRequestMapperCustom.selectPurchaseRequests(form,
                loginUser.getPrViewScope(), loginUser.getUsername());
        dto.setPurchaseRequests(prs);
        dto.setCurrentPage(page);
        dto.setPageNumberList(CommonUtil.createPageNumbers(purchaseRequestMapperCustom.countPurchaseRequests(form,
                loginUser.getPrViewScope(), loginUser.getUsername()), form.getSize(), page, 2));

        logger.debug("PR一覧取得完了");
        return dto;
    }

    public PurchaseRequestDetailDto getPurchaseRequestDetail(String prId, CustomUserDetails loginUser) {
        logger.debug("PR詳細取得開始");

        PurchaseRequestDetailDto dto = purchaseRequestMapperCustom.selectPurchaseRequestDetailHeader(prId,
                loginUser.getPrViewScope(), loginUser.getUsername());
        dto.setDetails(purchaseRequestDetailMapperCustom.selectPurchaseRequestDetailLines(prId,
                loginUser.getPrViewScope(), loginUser.getUsername()));

        logger.debug("PR詳細取得完了");

        return dto;
    }

    @Transactional
    public String create(String userId, PurchaseRequestCreateForm form) {
        logger.info("PR作成処理開始");

        String prId = UUID.randomUUID().toString();
        // 明細作成
        List<PurchaseRequestDetail> details = toCreateDetailEntities(prId, form.getDetails());

        int totalExcludingTax = details.stream().mapToInt(i -> i.getSubtotalExcludingTax()).sum();

        // ヘッダー登録
        PurchaseRequest header = new PurchaseRequest();

        header.setPrId(prId);
        header.setRequesterUserId(userId);
        header.setDueDate(form.getDueDate());
        header.setTotalAmountExcludingTax(totalExcludingTax);
        header.setStatus(PurchaseRequestStatus.PENDING);
        header.setNote(form.getNote());
        purchaseRequestMapper.insertSelective(header);

        // 明細登録
        purchaseRequestDetailMapperCustom.bulkInsert(details);

        logger.info("PR作成処理完了");

        return prId;

    }

    public PurchaseRequestCreateViewDto prepareCreateView(String userId) {
        logger.debug("PR作成画面表示情報取得開始");

        PurchaseRequestCreateViewDto dto = new PurchaseRequestCreateViewDto();
        dto.setRequester(usersMapperCustom.selectFullName(userId));
        dto.setUnitOptions(unitMapperCustom.selectUnitOptions());
        dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());

        logger.debug("PR作成画面表示情報取得完了");
        return dto;
    }

    public PurchaseRequestEditViewDto prepareEditView(String prId) {
        logger.debug("PR編集画面表示情報取得開始");

        PurchaseRequestEditViewDto dto = purchaseRequestMapperCustom.selectPurchaseRequestEditView(prId);
        dto.setUnitOptions(unitMapperCustom.selectUnitOptions());
        dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());

        logger.debug("PR編集画面表示情報取得完了");

        return dto;
    }

    @Transactional
    public void update(String prId, PurchaseRequestEditForm form) {
        logger.info("PR更新処理開始");

        // 明細変換
        List<PurchaseRequestDetail> detailEntities = toEditDetailEntities(prId, form.getDetails());

        int totalExcludingTax = detailEntities.stream().mapToInt(i -> i.getSubtotalExcludingTax()).sum();

        // ヘッダー更新
        PurchaseRequest header = new PurchaseRequest();
        header.setPrId(prId);
        header.setDueDate(form.getDueDate());
        header.setNote(form.getNote());
        header.setTotalAmountExcludingTax(totalExcludingTax);
        purchaseRequestMapper.updateByPrimaryKeySelective(header);

        // 明細更新
        purchaseRequestDetailMapperCustom.bulkUpsert(detailEntities);

        // 明細削除
        form.getDeletedPrDetailIds().forEach(id -> {
            purchaseRequestDetailMapper.deleteByPrimaryKey(id);
        });

        logger.info("PR更新処理完了");

    }

    private List<PurchaseRequestDetail> toCreateDetailEntities(String prId,
            List<PurchaseRequestDetailCreateForm> details) {
        List<PurchaseRequestDetail> list = new ArrayList<PurchaseRequestDetail>();

        int lineNo = 1;
        for (PurchaseRequestDetailCreateForm form : details) {
            PurchaseRequestDetail prd = new PurchaseRequestDetail();
            if (form.getDetailInputType() == DetailInputType.CATALOG) {
                prd = createCatalogItemSnap(form.getItemId());
            }
            else if (form.getDetailInputType() == DetailInputType.FREE) {
                prd.setItemId(null);
                prd.setSnapItemName(form.getItemName());
                prd.setSnapKind(form.getKind());
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

    private List<PurchaseRequestDetail> toEditDetailEntities(String prId, List<PurchaseRequestDetailEditForm> details) {
        List<PurchaseRequestDetail> list = new ArrayList<PurchaseRequestDetail>();
        int lineNo = 1;

        for (PurchaseRequestDetailEditForm form : details) {
            PurchaseRequestDetail prd = new PurchaseRequestDetail();

            if (form.getDetailInputType() == DetailInputType.CATALOG) {
                // 新規行
                if (StringUtils.isBlank(form.getPrDetailId())) {
                    prd = createCatalogItemSnap(form.getItemId());
                }
                else {
                    prd.setItemId(form.getItemId());
                    prd.setSnapUnitPrice(form.getPrice());
                }
            }
            else if (form.getDetailInputType() == DetailInputType.FREE) {
                prd.setItemId(null);
                prd.setSnapItemName(form.getItemName());
                prd.setSnapKind(form.getKind());
                prd.setUnitId(form.getUnitId());
                prd.setSnapUnitName(form.getUnitName());
                prd.setSupplierId(form.getSupplierId());
                prd.setSnapSupplierName(form.getSupplierName());
                prd.setSnapUnitPrice(form.getPrice());

            }
            // 共通
            prd.setPrDetailId(form.getPrDetailId());
            prd.setPrId(prId);
            prd.setLineNo(lineNo++);
            prd.setQuantity(form.getQuantity());
            prd.setSubtotalExcludingTax(prd.getQuantity() * prd.getSnapUnitPrice());

            list.add(prd);
        }

        return list;
    }

    private PurchaseRequestDetail createCatalogItemSnap(String itemId) {
        PurchaseRequestDetail prd = new PurchaseRequestDetail();
        CatalogItemSnapDto dto = itemMapperCustom.selectCatalogItemSnap(itemId);
        prd.setItemId(itemId);
        prd.setSnapItemName(dto.getItemName());
        prd.setSnapKind(dto.getKind());
        prd.setUnitId(dto.getUnitId());
        prd.setSnapUnitName(dto.getUnitName());
        prd.setSupplierId(dto.getSupplierId());
        prd.setSnapSupplierName(dto.getSupplierName());
        prd.setSnapUnitPrice(dto.getPrice());

        return prd;
    }

}
