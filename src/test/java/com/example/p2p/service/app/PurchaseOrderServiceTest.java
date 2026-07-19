package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.AuthenticationUserDto;
import com.example.p2p.dto.app.PurchaseOrderCreateViewDto;
import com.example.p2p.dto.app.PurchaseOrderDetailDto;
import com.example.p2p.dto.app.PurchaseOrderLineViewDto;
import com.example.p2p.dto.app.PurchaseOrderLineViewDto.PrDetailAllocationViewDto;
import com.example.p2p.dto.app.PurchaseOrderSupplierSelectionViewDto;
import com.example.p2p.dto.app.RelatedPrDto;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.entity.PurchaseRequestDetail;
import com.example.p2p.entity.PurchaseRequestPurchaseOrder;
import com.example.p2p.entity.Supplier;
import com.example.p2p.entity.Users;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.PurchaseOrderStatus;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.app.PurchaseOrderSupplierSelectionSearchForm;
import com.example.p2p.mapper.PurchaseRequestMapper;
import com.example.p2p.mapper.PurchaseRequestDetailMapper;
import com.example.p2p.mapper.PurchaseRequestPurchaseOrderMapper;
import com.example.p2p.mapper.SupplierMapper;
import com.example.p2p.mapper.UsersMapper;
import com.example.p2p.security.CustomUserDetails;
import com.example.p2p.session.app.PurchaseOrderCreateDraft;
import com.example.p2p.session.app.PurchaseOrderCreateDraft.SelectedPurchaseRequestDetail;

@SpringBootTest
@Transactional
class PurchaseOrderServiceTest {

    @Autowired
    PurchaseOrderService purchaseOrderService;

    @Autowired
    PurchaseRequestPurchaseOrderMapper purchaseRequestPurchaseOrderMapper;

    @Autowired
    PurchaseRequestMapper purchaseRequestMapper;

    @Autowired
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    @Autowired
    SupplierMapper supplierMapper;

    @Autowired
    UsersMapper usersMapper;

    @Test
    void getPurchaseOrderDetail_standardPo() {
        String poId = "f7e34df9-f4cb-4c88-82c2-1fdfc7dd8a03";

        PurchaseRequestPurchaseOrder relation = new PurchaseRequestPurchaseOrder();
        relation.setPrId("3a5b130e-0279-4bca-bee3-d41cddbc9192");
        relation.setPoId(poId);
        purchaseRequestPurchaseOrderMapper.insertSelective(relation);

        PurchaseOrderDetailDto actual = purchaseOrderService.getPurchaseOrderDetail(poId, loginUser());

        assertThat(actual.getPoId()).isEqualTo(poId);
        assertThat(actual.getDisplayNumber()).isEqualTo(3);
        assertThat(actual.getOrderType()).isEqualTo(PurchaseOrderType.STANDARD);
        assertThat(actual.getPurchaser()).isEqualTo("鈴木 一郎");
        assertThat(actual.getSupplierName()).isEqualTo("神奈川文具株式会社");
        assertThat(actual.getTotalAmountExcludingTax()).isEqualTo(6340);
        assertThat(actual.getStatus()).isEqualTo(PurchaseOrderStatus.APPROVED);
        assertThat(actual.getNote()).isEqualTo("PRから作成。同一PR内の神奈川文具分を標準発注として分割。");
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUserId()).isEqualTo("6fe99043-cbd1-49c0-96d4-c156c58a8e60");
        assertThat(actual.getDeliveryDueDate()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(actual.getServicePeriodFrom()).isNull();
        assertThat(actual.getServicePeriodTo()).isNull();
        assertThat(actual.getRelatedPrs()).hasSize(2);
        assertThat(actual.getDetails()).hasSize(2);
        assertThat(actual.getApprovalProgressSteps()).hasSize(3);
        assertThat(actual.getCurrentStepOrder()).isEqualTo(3);
    }

    @Test
    void searchSupplierSelections_standard() {
        insertFreeInputGoodsDetail();

        PurchaseOrderSupplierSelectionViewDto actual = purchaseOrderService
            .searchSupplierSelections(PurchaseOrderType.STANDARD, new PurchaseOrderSupplierSelectionSearchForm());

        assertThat(actual.getSupplierOptions()).hasSize(3);
        assertThat(actual.getSupplierSelections()).hasSize(3);
        assertThat(actual.getSupplierSelections().get(0).getSupplierId()).isNull();
        assertThat(actual.getSupplierSelections().get(0).getPurchaseRequests())
            .extracting(RelatedPrDto::getPrId, RelatedPrDto::getDisplayNumber)
            .containsExactly(tuple("56856dfe-8e7a-4524-9d05-9e161c6b8fc3", 4));
    }

    @Test
    void getPurchaseOrderCreateView_standard_groupsDetailsWithSameLineKey() {
        String supplierId = "a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c";
        String purchaserUserId = "6fe99043-cbd1-49c0-96d4-c156c58a8e60";
        Supplier supplier = supplierMapper.selectByPrimaryKey(supplierId);
        Users purchaser = usersMapper.selectByPrimaryKey(purchaserUserId);
        PurchaseRequest firstPr = insertPurchaseRequest("60000000-0000-4000-9000-000000000101");
        PurchaseRequest secondPr = insertPurchaseRequest("60000000-0000-4000-9000-000000000201");

        PurchaseRequestDetail firstDetail = insertPurchaseRequestDetail(
                "60000000-0000-4000-9000-000000000111", firstPr.getPrId(), 1, ItemKind.GOODS, supplierId,
                supplier.getName(), "a5c1b32a-7b01-49d3-8fef-48e0f39dc31f", "Aggregated Goods Item",
                "22222222-2222-2222-2222-222222222221", "piece", 1200, 10);
        PurchaseRequestDetail secondDetail = insertPurchaseRequestDetail(
                "60000000-0000-4000-9000-000000000211", secondPr.getPrId(), 1, ItemKind.GOODS, supplierId,
                supplier.getName(), "a5c1b32a-7b01-49d3-8fef-48e0f39dc31f", "Aggregated Goods Item",
                "22222222-2222-2222-2222-222222222221", "piece", 1200, 20);
        PurchaseRequestDetail thirdDetail = insertPurchaseRequestDetail(
                "60000000-0000-4000-9000-000000000112", firstPr.getPrId(), 2, ItemKind.GOODS, supplierId,
                supplier.getName(), "1bd0d872-69b1-4999-b522-ac202c481662", "Different Goods Item",
                "22222222-2222-2222-2222-222222222221", "box", 2500, 30);

        int firstSelectedQuantity = 4;
        PurchaseOrderCreateDraft draft = new PurchaseOrderCreateDraft(supplierId, supplier.getName(),
                PurchaseOrderType.STANDARD,
                List.of(new SelectedPurchaseRequestDetail(secondDetail.getPrDetailId(), 5),
                        new SelectedPurchaseRequestDetail(thirdDetail.getPrDetailId(), 6),
                        new SelectedPurchaseRequestDetail(firstDetail.getPrDetailId(), firstSelectedQuantity)));

        PurchaseOrderCreateViewDto actual = purchaseOrderService.getPurchaseOrderCreateView(draft, purchaserUserId);

        assertThat(actual.getSupplierId()).isEqualTo(supplierId);
        assertThat(actual.getSupplierName()).isEqualTo(supplier.getName());
        assertThat(actual.getPurchaser())
            .isEqualTo(purchaser.getLastName() + " " + purchaser.getFirstName());
        assertThat(actual.getRelatedPrNumbers())
            .containsExactly(firstPr.getDisplayNumber(), secondPr.getDisplayNumber());
        assertThat(actual.getLines()).hasSize(2);

        PurchaseOrderLineViewDto aggregatedLine = actual.getLines().get(0);
        assertLine(aggregatedLine, firstDetail);
        assertThat(aggregatedLine.getAllocations()).hasSize(2);
        assertAllocation(aggregatedLine.getAllocations().get(0), firstDetail, firstSelectedQuantity);

        PurchaseOrderLineViewDto differentLine = actual.getLines().get(1);
        assertLine(differentLine, thirdDetail);
        assertThat(differentLine.getAllocations()).hasSize(1);
    }

    @Test
    void getPurchaseOrderCreateView_service_doesNotGroupDetailsWithSameLineKey() {
        String supplierName = "Free Input Service Supplier";
        String purchaserUserId = "6fe99043-cbd1-49c0-96d4-c156c58a8e60";
        PurchaseRequest request = insertPurchaseRequest("70000000-0000-4000-9000-000000000101");

        PurchaseRequestDetail firstDetail = insertPurchaseRequestDetail(
                "70000000-0000-4000-9000-000000000111", request.getPrId(), 1, ItemKind.SERVICE, null, supplierName,
                "d21fb363-afb3-4911-a051-fac108a06658", "Same Service Item",
                "22222222-2222-2222-2222-222222222224", "job", 8000, 2);
        PurchaseRequestDetail secondDetail = insertPurchaseRequestDetail(
                "70000000-0000-4000-9000-000000000112", request.getPrId(), 2, ItemKind.SERVICE, null, supplierName,
                "d21fb363-afb3-4911-a051-fac108a06658", "Same Service Item",
                "22222222-2222-2222-2222-222222222224", "job", 8000, 3);
        PurchaseRequestDetail thirdDetail = insertPurchaseRequestDetail(
                "70000000-0000-4000-9000-000000000113", request.getPrId(), 3, ItemKind.SERVICE, null, supplierName,
                "2cc30fd9-9dae-4abe-a145-b280d9de2f38", "Different Service Item",
                "22222222-2222-2222-2222-222222222225", "hour", 12000, 4);

        PurchaseOrderCreateDraft draft = new PurchaseOrderCreateDraft(null, supplierName, PurchaseOrderType.SERVICE,
                List.of(new SelectedPurchaseRequestDetail(firstDetail.getPrDetailId(), null),
                        new SelectedPurchaseRequestDetail(secondDetail.getPrDetailId(), null),
                        new SelectedPurchaseRequestDetail(thirdDetail.getPrDetailId(), null)));

        PurchaseOrderCreateViewDto actual = purchaseOrderService.getPurchaseOrderCreateView(draft, purchaserUserId);

        assertThat(actual.getLines()).hasSize(3);
        assertThat(actual.getLines()).allSatisfy(line -> assertThat(line.getAllocations()).hasSize(1));
        assertAllocation(actual.getLines().get(0).getAllocations().get(0), firstDetail, null);
        assertAllocation(actual.getLines().get(1).getAllocations().get(0), secondDetail, null);
        assertAllocation(actual.getLines().get(2).getAllocations().get(0), thirdDetail, null);
    }

    private PurchaseRequest insertPurchaseRequest(String prId) {
        PurchaseRequest request = new PurchaseRequest();
        request.setPrId(prId);
        request.setRequesterUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
        request.setDueDate(LocalDate.of(2026, 8, 1));
        request.setTotalAmountExcludingTax(0);
        request.setStatus(PurchaseRequestStatus.APPROVED);
        request.setCurrentStepOrder(1);
        purchaseRequestMapper.insertSelective(request);
        return purchaseRequestMapper.selectByPrimaryKey(prId);
    }

    private PurchaseRequestDetail insertPurchaseRequestDetail(String prDetailId, String prId, int lineNo,
            ItemKind kind, String supplierId, String supplierName, String itemId, String itemName, String unitId,
            String unitName, int unitPrice, int quantity) {
        PurchaseRequestDetail detail = new PurchaseRequestDetail();
        detail.setPrDetailId(prDetailId);
        detail.setPrId(prId);
        detail.setLineNo(lineNo);
        detail.setSnapKind(kind);
        detail.setSupplierId(supplierId);
        detail.setSnapSupplierName(supplierName);
        detail.setItemId(itemId);
        detail.setSnapItemName(itemName);
        detail.setUnitId(unitId);
        detail.setSnapUnitName(unitName);
        detail.setSnapUnitPrice(unitPrice);
        detail.setQuantity(quantity);
        detail.setSubtotalExcludingTax(unitPrice * quantity);
        purchaseRequestDetailMapper.insertSelective(detail);
        return purchaseRequestDetailMapper.selectByPrimaryKey(prDetailId);
    }

    private void assertLine(PurchaseOrderLineViewDto actual, PurchaseRequestDetail expected) {
        assertThat(actual.getItemId()).isEqualTo(expected.getItemId());
        assertThat(actual.getUnitId()).isEqualTo(expected.getUnitId());
        assertThat(actual.getItemName()).isEqualTo(expected.getSnapItemName());
        assertThat(actual.getUnitName()).isEqualTo(expected.getSnapUnitName());
        assertThat(actual.getUnitPrice()).isEqualTo(expected.getSnapUnitPrice());
    }

    private void assertAllocation(PrDetailAllocationViewDto actual, PurchaseRequestDetail expected,
            Integer expectedOrderQuantity) {
        assertThat(actual.getPrDetailId()).isEqualTo(expected.getPrDetailId());
        assertThat(actual.getPurchaseRequestQuantity()).isEqualTo(expected.getQuantity());
        assertThat(actual.getOrderQuantity()).isEqualTo(expectedOrderQuantity);
    }

    private void insertFreeInputGoodsDetail() {
        PurchaseRequest request = new PurchaseRequest();
        request.setPrId("56856dfe-8e7a-4524-9d05-9e161c6b8fc3");
        request.setStatus(PurchaseRequestStatus.APPROVED);
        purchaseRequestMapper.updateByPrimaryKeySelective(request);

        PurchaseRequestDetail freeInput = new PurchaseRequestDetail();
        freeInput.setPrId("56856dfe-8e7a-4524-9d05-9e161c6b8fc3");
        freeInput.setSnapItemName("Free Input Item");
        freeInput.setSnapKind(ItemKind.GOODS);
        freeInput.setSnapUnitName("piece");
        freeInput.setSnapSupplierName("Free Input Supplier");
        freeInput.setQuantity(2);
        freeInput.setSnapUnitPrice(1234);
        freeInput.setSubtotalExcludingTax(2468);
        freeInput.setLineNo(3);
        purchaseRequestDetailMapper.insertSelective(freeInput);
    }

    private CustomUserDetails loginUser() {
        AuthenticationUserDto authUser = new AuthenticationUserDto();
        authUser.setUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
        authUser.setPasswordHash("$2a$10$dummyPasswordHash");
        authUser.setEmail("test@example.com");
        authUser.setRoleName("ADMIN");
        authUser.setPrCreate(true);
        authUser.setPoCreate(true);
        authUser.setReceiptCreate(true);
        authUser.setInvoiceCreate(false);
        authUser.setPrViewScope(VisibilityScope.ALL);
        authUser.setPoViewScope(VisibilityScope.ALL);
        authUser.setReceiptViewScope(VisibilityScope.SELF);
        authUser.setInvoiceViewScope(VisibilityScope.NONE);
        authUser.setPrApprove(true);
        authUser.setPoApprove(true);
        authUser.setInvoiceApprove(false);
        authUser.setSettingManage(true);

        return new CustomUserDetails(authUser);
    }
}
