package com.example.p2p.service.app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.app.PurchaseOrderCreateDetailDto;
import com.example.p2p.dto.app.PurchaseOrderCreateSourceDto;
import com.example.p2p.dto.app.PurchaseOrderCreateViewDto;
import com.example.p2p.dto.app.PurchaseOrderDetailDto;
import com.example.p2p.dto.app.PurchaseOrderDetailSelectionViewDto;
import com.example.p2p.dto.app.PurchaseOrderLineViewDto;
import com.example.p2p.dto.app.PurchaseOrderLineViewDto.PrDetailAllocationViewDto;
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
import com.example.p2p.session.app.PurchaseOrderCreateDraft;
import com.example.p2p.session.app.PurchaseOrderCreateDraft.SelectedPurchaseRequestDetail;
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

    private ModelMapper modelMapper;

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
        logger.debug("発注書仕入先選択情報取得開始");

        ItemKind kind = orderType == PurchaseOrderType.STANDARD ? ItemKind.GOODS : ItemKind.SERVICE;

        PurchaseOrderSupplierSelectionViewDto dto = new PurchaseOrderSupplierSelectionViewDto();
        dto.setSupplierSelections(purchaseOrderMapperCustom.selectPurchaseOrderSupplierSelections(kind, form));
        dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());

        logger.debug("発注書仕入先選択情報取得完了");

        return dto;

    }

    public PurchaseOrderDetailSelectionViewDto getDetailSelectionView(PurchaseOrderType orderType, String supplierId,
            String supplierName) {
        logger.debug("発注明細選択情報取得開始");

        ItemKind kind = orderType == PurchaseOrderType.STANDARD ? ItemKind.GOODS : ItemKind.SERVICE;
        PurchaseOrderDetailSelectionViewDto dto = purchaseOrderMapperCustom
            .selectPurchaseOrderDetailSelectionView(supplierId, supplierName, kind);

        logger.debug("発注明細選択情報取得完了");

        return dto;
    }

    public PurchaseOrderCreateViewDto getPurchaseOrderCreateView(PurchaseOrderCreateDraft draft, String userId) {
        logger.debug("発注書作成画面表示情報取得開始");

        PurchaseOrderCreateSourceDto source = purchaseOrderMapperCustom.selectPurchaseOrderCreateSource(
                draft.getSupplierId(), draft.getSupplierName(), userId,
                draft.getDetails().stream().map(SelectedPurchaseRequestDetail::getPrDetailId).toList());

        Map<String, SelectedPurchaseRequestDetail> selectedDetailsByPrDetailId = draft.getDetails()
            .stream()
            .collect(Collectors.toMap(SelectedPurchaseRequestDetail::getPrDetailId, Function.identity()));

        // リクエストデータとDBから取得したデータをマージする
        // 若いPR番号→若い行番号順に入ってる
        List<PurchaseOrderCreateDetailDto> mergedDetails = source.getDetails().stream().map(d -> {
            PurchaseOrderCreateDetailDto dto = new PurchaseOrderCreateDetailDto();
            modelMapper.map(d, dto);
            dto.setOrderQuantity(selectedDetailsByPrDetailId.get(d.getPrDetailId()).getSelectedQuantity());
            return dto;
        }).toList();

        List<List<PurchaseOrderCreateDetailDto>> detailGroups = createDetailGroups(draft.getOrderType(), mergedDetails);

        List<PurchaseOrderLineViewDto> poLines = detailGroups.stream().map(this::toPurchaseOrderLineView).toList();

        PurchaseOrderCreateViewDto view = new PurchaseOrderCreateViewDto();
        view.setSupplierId(draft.getSupplierId());
        view.setSupplierName(source.getSupplierName());
        view.setPurchaser(source.getPurchaser());
        view.setRelatedPrNumbers(source.getRelatedPrNumbers());
        view.setLines(poLines);

        logger.debug("発注書作成画面表示情報取得完了");

        return view;

    }

    private List<List<PurchaseOrderCreateDetailDto>> createDetailGroups(PurchaseOrderType orderType,
            List<PurchaseOrderCreateDetailDto> details) {
        // 物品発注の場合
        if (orderType == PurchaseOrderType.STANDARD) {
            Map<Object, List<PurchaseOrderCreateDetailDto>> detailsByLineKey = details.stream()
                .collect(
                        Collectors
                            .groupingBy(
                                    d -> new PurchaseOrderLineGroupKey(d.getItemId(), d.getUnitId(), d.getItemName(),
                                            d.getUnitName(), d.getUnitPrice()),
                                    LinkedHashMap::new, Collectors.toList()));
            return new ArrayList<List<PurchaseOrderCreateDetailDto>>(detailsByLineKey.values());
        }
        // サービス発注の場合
        return details.stream().map(d -> List.of(d)).toList();
    }

    private PurchaseOrderLineViewDto toPurchaseOrderLineView(List<PurchaseOrderCreateDetailDto> groupedDetails) {
        PurchaseOrderLineViewDto lineView = new PurchaseOrderLineViewDto();
        PurchaseOrderCreateDetailDto representativeDetail = groupedDetails.get(0);
        // 共通部分のマッピング
        modelMapper.map(representativeDetail, lineView);

        // 集約明細のセット
        List<PrDetailAllocationViewDto> allocations = groupedDetails.stream()
            .map(d -> modelMapper.map(d, PrDetailAllocationViewDto.class))
            .toList();
        lineView.setAllocations(allocations);

        return lineView;
    }

    private record PurchaseOrderLineGroupKey(String itemId, String unitId, String itemName, String unitName,
            int unitPrice) {
    }

}
