package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.AuthenticationUserDto;
import com.example.p2p.dto.app.PurchaseOrderDetailDto;
import com.example.p2p.entity.PurchaseRequestPurchaseOrder;
import com.example.p2p.enums.PurchaseOrderStatus;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.mapper.PurchaseRequestPurchaseOrderMapper;
import com.example.p2p.security.CustomUserDetails;

@SpringBootTest
@Transactional
class PurchaseOrderServiceTest {

    @Autowired
    PurchaseOrderService purchaseOrderService;

    @Autowired
    PurchaseRequestPurchaseOrderMapper purchaseRequestPurchaseOrderMapper;

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
