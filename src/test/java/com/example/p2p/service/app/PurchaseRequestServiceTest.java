package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.PurchaseRequestDetailLineDto;
import com.example.p2p.dto.app.PurchaseRequestListViewDto;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.entity.PurchaseRequestDetail;
import com.example.p2p.entity.PurchaseRequestDetailExample;
import com.example.p2p.enums.DetailInputType;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.form.app.PurchaseRequestCreateForm;
import com.example.p2p.form.app.PurchaseRequestDetailForm;
import com.example.p2p.form.app.PurchaseRequestSearchForm;
import com.example.p2p.mapper.PurchaseRequestDetailMapper;
import com.example.p2p.mapper.PurchaseRequestMapper;

@SpringBootTest
@Transactional
class PurchaseRequestServiceTest {

    @Autowired
    PurchaseRequestService purchaseRequestService;
    
    @Autowired
    PurchaseRequestMapper purchaseRequestMapper;

    @Autowired
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    @Test
    void searchPurchaseRequests() {
        PurchaseRequestListViewDto actual = purchaseRequestService
            .searchPurchaseRequests(new PurchaseRequestSearchForm());

        assertThat(actual.getCurrentPage()).isOne();
        assertThat(actual.getPageNumberList()).isEqualTo(List.of(1, 2, 3));
        assertThat(actual.getPurchaseRequests()).hasSize(2);

        assertThat(actual.getPurchaseRequests().get(0).getPrId()).isEqualTo("88bfbcf6-2be6-4d31-8a46-155a7b58ab93");

    }

    @Test
    void getPurchaseRequestDetail() {
        PurchaseRequestDetailDto actual = purchaseRequestService
            .getPurchaseRequestDetail("3c4f62bf-855b-4c35-b19d-eb06acb16896");

        assertThat(actual.getDisplayNumber()).isEqualTo(3);
        assertThat(actual.getRequester()).isEqualTo("鈴木 一郎");
        assertThat(actual.getDueDate()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(actual.getTotalAmountExcludingTax()).isEqualTo(39940);
        assertThat(actual.getStatus()).isEqualTo(PurchaseRequestStatus.APPROVED);
        assertThat(actual.getNote()).isEqualTo("test");
        assertThat(actual.getCreatedAt()).isEqualTo(LocalDate.of(2026, 4, 21));

        assertThat(actual.getDetails()).hasSize(3);
        assertThat(actual.getDetails()).extracting(PurchaseRequestDetailLineDto::getItemName)
            .containsExactlyInAnyOrder("24インチ液晶モニター", "A4コピー用紙 500枚", "油性ボールペン 黒 10本セット");
        PurchaseRequestDetailLineDto first = actual.getDetails()
            .stream()
            .filter(detail -> detail.getItemName().equals("24インチ液晶モニター"))
            .findFirst()
            .orElseThrow();

        assertThat(first.getItemName()).isEqualTo("24インチ液晶モニター");
        assertThat(first.getKind()).isEqualTo(ItemKind.GOODS);
        assertThat(first.getUnitName()).isEqualTo("台");
        assertThat(first.getSupplierName()).isEqualTo("関西オフィスサービス株式会社");
        assertThat(first.getUnitPrice()).isEqualTo(16800);
        assertThat(first.getQuantity()).isEqualTo(2);
        assertThat(first.getSubtotalExcludingTax()).isEqualTo(33600);
    }

    @Nested
    class Create {

        String userId = "6fe99043-cbd1-49c0-96d4-c156c58a8e60";

        @Test
        void create_one() {
            PurchaseRequestCreateForm form = new PurchaseRequestCreateForm();
            form.setDueDate(LocalDate.of(2027, 3, 2));
            form.setNote("testノート");

            PurchaseRequestDetailForm prdf = new PurchaseRequestDetailForm();
            prdf.setDetailInputType(DetailInputType.CATALOG);
            prdf.setItemId("1bd0d872-69b1-4999-b522-ac202c481662");
            prdf.setQuantity(2);
            form.setDetails(List.of(prdf));

            UUID fixedPrId = UUID.fromString("3e817ae3-c770-4e43-afc3-4076e7bf1917");
            try (MockedStatic<UUID> mocked = mockStatic(UUID.class)) {
                mocked.when(UUID::randomUUID).thenReturn(fixedPrId);

                purchaseRequestService.create(userId, form);
            }
            
            PurchaseRequest actualHeader = purchaseRequestMapper.selectByPrimaryKey(fixedPrId.toString());
            assertThat(actualHeader.getDisplayNumber()).isNotNull();
            assertThat(actualHeader.getRequesterUserId()).isEqualTo(userId);
            assertThat(actualHeader.getDueDate()).isEqualTo(LocalDate.of(2027, 3, 2));
            assertThat(actualHeader.getTotalAmountExcludingTax()).isEqualTo(1960);
            assertThat(actualHeader.getStatus()).isEqualTo(PurchaseRequestStatus.PENDING.toString());
            assertThat(actualHeader.getNote()).isEqualTo("testノート");
            assertThat(actualHeader.getCreatedAt()).isNotNull();
            assertThat(actualHeader.getUpdatedAt()).isNotNull();

            PurchaseRequestDetailExample ex = new PurchaseRequestDetailExample();
            ex.createCriteria().andPrIdEqualTo(fixedPrId.toString());
            List<PurchaseRequestDetail> actualDetails = purchaseRequestDetailMapper.selectByExample(ex);
            assertThat(actualDetails).hasSize(1);
            PurchaseRequestDetail detail = actualDetails.get(0);
            
            assertThat(detail.getPrDetailId()).isNotBlank();
            assertThat(detail.getPrId()).isEqualTo("3e817ae3-c770-4e43-afc3-4076e7bf1917");
            assertThat(detail.getLineNo()).isOne();
            assertThat(detail.getItemId()).isEqualTo("1bd0d872-69b1-4999-b522-ac202c481662");
            assertThat(detail.getSnapItemName()).isEqualTo("油性ボールペン 黒 10本セット");
            assertThat(detail.getSnapKind()).isEqualTo(ItemKind.GOODS.toString());
            assertThat(detail.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222221");
            assertThat(detail.getSnapUnitName()).isEqualTo("個");
            assertThat(detail.getSupplierId()).isEqualTo("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            assertThat(detail.getSnapSupplierName()).isEqualTo("神奈川文具株式会社");
            assertThat(detail.getQuantity()).isEqualTo(2);
            assertThat(detail.getSnapUnitPrice()).isEqualTo(980);
            assertThat(detail.getSubtotalExcludingTax()).isEqualTo(1960);
            assertThat(detail.getCreatedAt()).isNotNull();
            assertThat(detail.getUpdatedAt()).isNotNull();
        }
        
        @Test
        void create_three() {
            PurchaseRequestCreateForm form = new PurchaseRequestCreateForm();

            PurchaseRequestDetailForm detailForm1 = new PurchaseRequestDetailForm();
            detailForm1.setDetailInputType(DetailInputType.CATALOG);
            detailForm1.setItemId("1bd0d872-69b1-4999-b522-ac202c481662");
            detailForm1.setQuantity(2);
            
            PurchaseRequestDetailForm detailForm2 = new PurchaseRequestDetailForm();
            detailForm2.setDetailInputType(DetailInputType.FREE);
            detailForm2.setItemId(null);
            detailForm2.setItemName("freeテスト");
            detailForm2.setKind(ItemKind.SERVICE);
            detailForm2.setSupplierId(null);
            detailForm2.setSupplierName("testサプライヤー");
            detailForm2.setUnitId(null);
            detailForm2.setUnitName("testユニット");
            detailForm2.setPrice(5550);
            detailForm2.setQuantity(1);
            
            PurchaseRequestDetailForm detailForm3 = new PurchaseRequestDetailForm();
            detailForm3.setDetailInputType(DetailInputType.CATALOG);
            detailForm3.setItemId("a5c1b32a-7b01-49d3-8fef-48e0f39dc31f");
            detailForm3.setQuantity(10);
            form.setDetails(List.of(detailForm1, detailForm2 ,detailForm3));

            UUID fixedPrId = UUID.fromString("3e817ae3-c770-4e43-afc3-4076e7bf1917");
            try (MockedStatic<UUID> mocked = mockStatic(UUID.class)) {
                mocked.when(UUID::randomUUID).thenReturn(fixedPrId);

                purchaseRequestService.create(userId, form);
            }
            
            PurchaseRequest actualHeader = purchaseRequestMapper.selectByPrimaryKey(fixedPrId.toString());
            assertThat(actualHeader.getDisplayNumber()).isNotNull();
            assertThat(actualHeader.getDueDate()).isNull();
            assertThat(actualHeader.getTotalAmountExcludingTax()).isEqualTo(14310);
            assertThat(actualHeader.getStatus()).isEqualTo(PurchaseRequestStatus.PENDING.toString());
            assertThat(actualHeader.getNote()).isNull();
            
            PurchaseRequestDetailExample ex = new PurchaseRequestDetailExample();
            ex.createCriteria().andPrIdEqualTo(fixedPrId.toString());
            ex.setOrderByClause("line_no asc");
            List<PurchaseRequestDetail> actualDetails = purchaseRequestDetailMapper.selectByExample(ex);
            assertThat(actualDetails).hasSize(3);
            assertThat(actualDetails).extracting(PurchaseRequestDetail::getLineNo)
            .containsExactly(1,2,3);
            
            PurchaseRequestDetail second = actualDetails.get(1);
            
            assertThat(second.getPrDetailId()).isNotBlank();
            assertThat(second.getPrId()).isEqualTo("3e817ae3-c770-4e43-afc3-4076e7bf1917");
            assertThat(second.getLineNo()).isEqualTo(2);
            assertThat(second.getItemId()).isNull();
            assertThat(second.getSnapItemName()).isEqualTo("freeテスト");
            assertThat(second.getSnapKind()).isEqualTo(ItemKind.SERVICE.toString());
            assertThat(second.getUnitId()).isNull();
            assertThat(second.getSnapUnitName()).isEqualTo("testユニット");
            assertThat(second.getSupplierId()).isNull();
            assertThat(second.getSnapSupplierName()).isEqualTo("testサプライヤー");
            assertThat(second.getQuantity()).isEqualTo(1);
            assertThat(second.getSnapUnitPrice()).isEqualTo(5550);
            assertThat(second.getSubtotalExcludingTax()).isEqualTo(5550);
            assertThat(second.getCreatedAt()).isNotNull();
            assertThat(second.getUpdatedAt()).isNotNull();
        }

    }

}
