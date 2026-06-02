package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.AuthenticationUserDto;
import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.PurchaseRequestDetailLineDto;
import com.example.p2p.dto.app.PurchaseRequestEditDetailDto;
import com.example.p2p.dto.app.PurchaseRequestEditViewDto;
import com.example.p2p.dto.app.PurchaseRequestListViewDto;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.entity.PurchaseRequestDetail;
import com.example.p2p.entity.PurchaseRequestDetailExample;
import com.example.p2p.entity.PurchaseRequestExample;
import com.example.p2p.enums.DetailInputType;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.app.PurchaseRequestCreateForm;
import com.example.p2p.form.app.PurchaseRequestDetailCreateForm;
import com.example.p2p.form.app.PurchaseRequestDetailEditForm;
import com.example.p2p.form.app.PurchaseRequestEditForm;
import com.example.p2p.form.app.PurchaseRequestSearchForm;
import com.example.p2p.mapper.PurchaseRequestDetailMapper;
import com.example.p2p.mapper.PurchaseRequestMapper;
import com.example.p2p.security.CustomUserDetails;

@SpringBootTest
@Transactional
class PurchaseRequestServiceTest {

    @Autowired
    PurchaseRequestService purchaseRequestService;

    @Autowired
    PurchaseRequestMapper purchaseRequestMapper;

    @MockitoSpyBean
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    @Test
    void searchPurchaseRequests() {
        AuthenticationUserDto authUser = new AuthenticationUserDto();
        authUser.setUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
        authUser.setPasswordHash("$2a$10$dummyPasswordHash");
        authUser.setEmail("test@example.com");
        authUser.setRoleName("管理者");

        authUser.setPrCreate(true);
        authUser.setPoCreate(true);
        authUser.setReceiptCreate(true);
        authUser.setInvoiceCreate(false);

        authUser.setPrViewScope(VisibilityScope.ALL);
        authUser.setPoViewScope(VisibilityScope.SELF);
        authUser.setReceiptViewScope(VisibilityScope.SELF);
        authUser.setInvoiceViewScope(VisibilityScope.NONE);

        authUser.setPrApprove(true);
        authUser.setPoApprove(false);
        authUser.setInvoiceApprove(false);
        authUser.setSettingManage(true);
        CustomUserDetails userDetails = new CustomUserDetails(authUser);

        PurchaseRequestListViewDto actual = purchaseRequestService
            .searchPurchaseRequests(new PurchaseRequestSearchForm(), userDetails);

        assertThat(actual.getCurrentPage()).isOne();
        assertThat(actual.getPageNumberList()).isEqualTo(List.of(1, 2, 3));
        assertThat(actual.getPurchaseRequests()).hasSize(2);

        assertThat(actual.getPurchaseRequests().get(0).getPrId()).isEqualTo("88bfbcf6-2be6-4d31-8a46-155a7b58ab93");

    }

    @Test
    void getPurchaseRequestDetail_viewAll() {
        AuthenticationUserDto authUser = new AuthenticationUserDto();
        authUser.setUserId("36a1d5d9-15b8-45d5-8ae7-607244bbe36e");
        authUser.setPasswordHash("$2a$10$dummyPasswordHash");
        authUser.setEmail("test@example.com");
        authUser.setRoleName("管理者");

        authUser.setPrCreate(true);
        authUser.setPoCreate(true);
        authUser.setReceiptCreate(true);
        authUser.setInvoiceCreate(false);

        authUser.setPrViewScope(VisibilityScope.ALL);
        authUser.setPoViewScope(VisibilityScope.SELF);
        authUser.setReceiptViewScope(VisibilityScope.SELF);
        authUser.setInvoiceViewScope(VisibilityScope.NONE);

        authUser.setPrApprove(true);
        authUser.setPoApprove(false);
        authUser.setInvoiceApprove(false);
        authUser.setSettingManage(true);

        CustomUserDetails loginUser = new CustomUserDetails(authUser);

        PurchaseRequestDetailDto actual = purchaseRequestService
            .getPurchaseRequestDetail("6b2c5959-233f-4b54-8a9b-98f4a1b13c40", loginUser);

        assertThat(actual.getDetails()).hasSize(2);
    }

    @Test
    void getPurchaseRequestDetail_viewSelf() {
        PurchaseRequest pr = new PurchaseRequest();
        pr.setPrId("3c4f62bf-855b-4c35-b19d-eb06acb16896");
        pr.setNote("test");
        pr.setCreatedAt(LocalDateTime.of(2026, 4, 21, 0, 0));
        purchaseRequestMapper.updateByPrimaryKeySelective(pr);

        AuthenticationUserDto authUser = new AuthenticationUserDto();
        authUser.setUserId("6fe99043-cbd1-49c0-96d4-c156c58a8e60");
        authUser.setPasswordHash("$2a$10$dummyPasswordHash");
        authUser.setEmail("test@example.com");
        authUser.setRoleName("管理者");

        authUser.setPrCreate(true);
        authUser.setPoCreate(true);
        authUser.setReceiptCreate(true);
        authUser.setInvoiceCreate(false);

        authUser.setPrViewScope(VisibilityScope.SELF);
        authUser.setPoViewScope(VisibilityScope.SELF);
        authUser.setReceiptViewScope(VisibilityScope.SELF);
        authUser.setInvoiceViewScope(VisibilityScope.NONE);

        authUser.setPrApprove(true);
        authUser.setPoApprove(false);
        authUser.setInvoiceApprove(false);
        authUser.setSettingManage(true);

        CustomUserDetails loginUser = new CustomUserDetails(authUser);

        PurchaseRequestDetailDto actual = purchaseRequestService
            .getPurchaseRequestDetail("3c4f62bf-855b-4c35-b19d-eb06acb16896", loginUser);

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

            PurchaseRequestDetailCreateForm prdf = new PurchaseRequestDetailCreateForm();
            prdf.setDetailInputType(DetailInputType.CATALOG);
            prdf.setItemId("1bd0d872-69b1-4999-b522-ac202c481662");
            prdf.setQuantity(2);
            form.setDetails(List.of(prdf));

            UUID fixedPrId = UUID.fromString("3e817ae3-c770-4e43-afc3-4076e7bf1917");
            try (MockedStatic<UUID> mocked = mockStatic(UUID.class)) {
                mocked.when(UUID::randomUUID).thenReturn(fixedPrId);

                String prId = purchaseRequestService.create(userId, form);

                assertThat(prId).isEqualTo(fixedPrId.toString());
            }

            PurchaseRequest actualHeader = purchaseRequestMapper.selectByPrimaryKey(fixedPrId.toString());
            assertThat(actualHeader.getDisplayNumber()).isNotNull();
            assertThat(actualHeader.getRequesterUserId()).isEqualTo(userId);
            assertThat(actualHeader.getDueDate()).isEqualTo(LocalDate.of(2027, 3, 2));
            assertThat(actualHeader.getTotalAmountExcludingTax()).isEqualTo(1960);
            assertThat(actualHeader.getStatus()).isEqualTo(PurchaseRequestStatus.PENDING);
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
            assertThat(detail.getSnapKind()).isEqualTo(ItemKind.GOODS);
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

            PurchaseRequestDetailCreateForm detailForm1 = new PurchaseRequestDetailCreateForm();
            detailForm1.setDetailInputType(DetailInputType.CATALOG);
            detailForm1.setItemId("1bd0d872-69b1-4999-b522-ac202c481662");
            detailForm1.setQuantity(2);

            PurchaseRequestDetailCreateForm detailForm2 = new PurchaseRequestDetailCreateForm();
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

            PurchaseRequestDetailCreateForm detailForm3 = new PurchaseRequestDetailCreateForm();
            detailForm3.setDetailInputType(DetailInputType.CATALOG);
            detailForm3.setItemId("a5c1b32a-7b01-49d3-8fef-48e0f39dc31f");
            detailForm3.setQuantity(10);
            form.setDetails(List.of(detailForm1, detailForm2, detailForm3));

            UUID fixedPrId = UUID.fromString("3e817ae3-c770-4e43-afc3-4076e7bf1917");
            try (MockedStatic<UUID> mocked = mockStatic(UUID.class)) {
                mocked.when(UUID::randomUUID).thenReturn(fixedPrId);

                purchaseRequestService.create(userId, form);
            }

            PurchaseRequest actualHeader = purchaseRequestMapper.selectByPrimaryKey(fixedPrId.toString());
            assertThat(actualHeader.getDisplayNumber()).isNotNull();
            assertThat(actualHeader.getDueDate()).isNull();
            assertThat(actualHeader.getTotalAmountExcludingTax()).isEqualTo(14310);
            assertThat(actualHeader.getStatus()).isEqualTo(PurchaseRequestStatus.PENDING);
            assertThat(actualHeader.getNote()).isNull();

            PurchaseRequestDetailExample ex = new PurchaseRequestDetailExample();
            ex.createCriteria().andPrIdEqualTo(fixedPrId.toString());
            ex.setOrderByClause("line_no asc");
            List<PurchaseRequestDetail> actualDetails = purchaseRequestDetailMapper.selectByExample(ex);
            assertThat(actualDetails).hasSize(3);
            assertThat(actualDetails).extracting(PurchaseRequestDetail::getLineNo).containsExactly(1, 2, 3);

            PurchaseRequestDetail second = actualDetails.get(1);

            assertThat(second.getPrDetailId()).isNotBlank();
            assertThat(second.getPrId()).isEqualTo("3e817ae3-c770-4e43-afc3-4076e7bf1917");
            assertThat(second.getLineNo()).isEqualTo(2);
            assertThat(second.getItemId()).isNull();
            assertThat(second.getSnapItemName()).isEqualTo("freeテスト");
            assertThat(second.getSnapKind()).isEqualTo(ItemKind.SERVICE);
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

    @Test
    void prepareEditView() {
        PurchaseRequest pr = new PurchaseRequest();
        pr.setPrId("3c4f62bf-855b-4c35-b19d-eb06acb16896");
        pr.setNote("test");
        pr.setCreatedAt(LocalDateTime.of(2026, 4, 21, 0, 0));
        purchaseRequestMapper.updateByPrimaryKeySelective(pr);

        PurchaseRequestDetail detail = new PurchaseRequestDetail();

        detail.setPrDetailId("fae1963c-2d5b-450a-8b12-4be480e65ecd");
        detail.setPrId("3c4f62bf-855b-4c35-b19d-eb06acb16896");
        detail.setItemId(null);
        detail.setSnapItemName("A4コピー用紙 500枚");
        detail.setSnapKind(ItemKind.SERVICE);
        detail.setUnitId("22222222-2222-2222-2222-222222222221");
        detail.setSnapUnitName("個");
        detail.setSupplierId("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
        detail.setSnapSupplierName("神奈川文具株式会社");
        detail.setQuantity(5);
        detail.setSnapUnitPrice(680);
        detail.setSubtotalExcludingTax(3400);
        detail.setCreatedAt(LocalDateTime.of(2026, 4, 26, 14, 40, 21, 434_000_000));
        detail.setUpdatedAt(LocalDateTime.of(2026, 5, 2, 19, 56, 0, 433_000_000));
        detail.setLineNo(3);

        int row = purchaseRequestDetailMapper.updateByPrimaryKey(detail);

        PurchaseRequestEditViewDto dto = purchaseRequestService.prepareEditView("3c4f62bf-855b-4c35-b19d-eb06acb16896");

        assertThat(dto.getPrId()).isEqualTo("3c4f62bf-855b-4c35-b19d-eb06acb16896");
        assertThat(dto.getStatus()).isEqualTo(PurchaseRequestStatus.APPROVED);
        assertThat(dto.getDisplayNumber()).isEqualTo(3);
        assertThat(dto.getRequester()).isEqualTo("鈴木 一郎");
        assertThat(dto.getCreatedAt()).isEqualTo(LocalDate.of(2026, 4, 21));
        assertThat(dto.getDueDate()).isEqualTo(LocalDate.of(2026, 5, 10));

        assertThat(dto.getTotalAmountExcludingTax()).isEqualTo(39940);
        assertThat(dto.getNote()).isEqualTo("test");

        assertThat(dto.getDetails()).extracting(PurchaseRequestEditDetailDto::getPrDetailId)
            .containsExactly("72a9f12f-bb87-4604-93f6-5d866542b0d8", "ad3e8b17-092a-4c73-acc4-1b799a5f5e97",
                    "fae1963c-2d5b-450a-8b12-4be480e65ecd");

        assertThat(dto.getUnitOptions()).hasSize(5);
        assertThat(dto.getSupplierOptions()).hasSize(3);

        PurchaseRequestEditDetailDto first = dto.getDetails().get(0);
        assertThat(first.getPrDetailId()).isEqualTo("72a9f12f-bb87-4604-93f6-5d866542b0d8");
        assertThat(first.getLineNo()).isOne();
        assertThat(first.getItemId()).isEqualTo("1bd0d872-69b1-4999-b522-ac202c481662");
        assertThat(first.getItemName()).isEqualTo("油性ボールペン 黒 10本セット");
        assertThat(first.getKind()).isEqualTo(ItemKind.GOODS);
        assertThat(first.getUnitName()).isEqualTo("個");
        assertThat(first.getSupplierName()).isEqualTo("神奈川文具株式会社");
        assertThat(first.getPrice()).isEqualTo(980);
        assertThat(first.getQuantity()).isEqualTo(3);
        assertThat(first.getDetailInputType()).isEqualTo(DetailInputType.CATALOG);

        assertThat(dto.getDetails().get(2).getDetailInputType()).isEqualTo(DetailInputType.FREE);

    }

    @Nested
    class Update {

        @BeforeEach
        void setup() {
            PurchaseRequest pr = new PurchaseRequest();
            pr.setPrId("6b2c5959-233f-4b54-8a9b-98f4a1b13c40");
            pr.setStatus(PurchaseRequestStatus.PENDING);
            purchaseRequestMapper.updateByPrimaryKeySelective(pr);
        }

        @EnumSource(value = PurchaseRequestStatus.class, names = { "CANCELLED", "COMPLETED" })
        @ParameterizedTest
        void update_whenInvalidStatus(PurchaseRequestStatus status) {
            String prId = "88bfbcf6-2be6-4d31-8a46-155a7b58ab93";
            PurchaseRequest pr = new PurchaseRequest();
            pr.setPrId(prId);
            pr.setStatus(status);
            purchaseRequestMapper.updateByPrimaryKeySelective(pr);

            assertThatThrownBy(() -> purchaseRequestService.update(prId, new PurchaseRequestEditForm()))
                .isInstanceOf(BusinessException.class);
        }

        @EnumSource(value = PurchaseRequestStatus.class, names = { "PENDING", "APPROVED", "REJECTED" })
        @ParameterizedTest
        void update_whenValidStatus(PurchaseRequestStatus status) {
            PurchaseRequest pr = new PurchaseRequest();
            String prId = "88bfbcf6-2be6-4d31-8a46-155a7b58ab93";
            pr.setPrId(prId);
            pr.setStatus(status);
            pr.setNote("test2");
            pr.setDueDate(LocalDate.of(2026, 5, 2));
            purchaseRequestMapper.updateByPrimaryKeySelective(pr);

            PurchaseRequestEditForm form = new PurchaseRequestEditForm();
            // PurchaseRequestDetailEditForm detailForm = new
            // PurchaseRequestDetailEditForm();
            // detailForm.setPrDetailId("33aaccfe-c7c4-4e37-ab48-259dbe7a7fdf");
            // detailForm.setDetailInputType(DetailInputType.CATALOG);
            // detailForm.setQuantity(2);
            // detailForm.setItemId("a5c1b32a-7b01-49d3-8fef-48e0f39dc31f");
            // detailForm.setKind(ItemKind.GOODS);
            // detailForm.setItemName("A4コピー用紙 500枚");
            // detailForm.setSupplierId("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            // detailForm.setSupplierName("神奈川文具株式会社");
            // detailForm.setUnitId("22222222-2222-2222-2222-222222222221");
            // detailForm.setUnitName("個");
            // detailForm.setPrice(680);

            purchaseRequestService.update(prId, form);

            PurchaseRequest actual = purchaseRequestMapper.selectByPrimaryKey(prId);
            assertThat(actual.getNote()).isNull();
            assertThat(actual.getDueDate()).isNull();

        }

        @Test
        void update_existingDetail() {
            PurchaseRequestEditForm form = new PurchaseRequestEditForm();
            PurchaseRequestDetailEditForm detailForm = new PurchaseRequestDetailEditForm();
            detailForm.setPrDetailId("33aaccfe-c7c4-4e37-ab48-259dbe7a7fdf");
            detailForm.setDetailInputType(DetailInputType.CATALOG);
            detailForm.setQuantity(2);
            detailForm.setItemId("a5c1b32a-7b01-49d3-8fef-48e0f39dc31f");
            detailForm.setKind(ItemKind.GOODS);
            detailForm.setItemName("A4コピー用紙 500枚");
            detailForm.setSupplierId("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            detailForm.setSupplierName("神奈川文具株式会社");
            detailForm.setUnitId("22222222-2222-2222-2222-222222222221");
            detailForm.setUnitName("個");
            detailForm.setPrice(680);
            form.setDetails(List.of(detailForm));
            form.setDueDate(LocalDate.of(2026, 5, 12));
            form.setNote("備考");

            purchaseRequestService.update("88bfbcf6-2be6-4d31-8a46-155a7b58ab93", form);

            PurchaseRequest updatedHeader = purchaseRequestMapper
                .selectByPrimaryKey("88bfbcf6-2be6-4d31-8a46-155a7b58ab93");
            assertThat(updatedHeader.getDisplayNumber()).isOne();
            assertThat(updatedHeader.getRequesterUserId()).isEqualTo("169f1e17-619f-45bf-b6dc-8faed08c404c");
            assertThat(updatedHeader.getDueDate()).isEqualTo(LocalDate.of(2026, 5, 12));
            assertThat(updatedHeader.getTotalAmountExcludingTax()).isEqualTo(1360);
            assertThat(updatedHeader.getStatus()).isEqualTo(PurchaseRequestStatus.PENDING);
            assertThat(updatedHeader.getCreatedAt()).isNotNull();
            assertThat(updatedHeader.getUpdatedAt()).isNotNull();
            assertThat(updatedHeader.getNote()).isEqualTo("備考");

            PurchaseRequestDetail updatedDetail = purchaseRequestDetailMapper
                .selectByPrimaryKey("33aaccfe-c7c4-4e37-ab48-259dbe7a7fdf");
            assertThat(updatedDetail.getPrId()).isEqualTo("88bfbcf6-2be6-4d31-8a46-155a7b58ab93");
            assertThat(updatedDetail.getItemId()).isEqualTo("a5c1b32a-7b01-49d3-8fef-48e0f39dc31f");
            assertThat(updatedDetail.getSnapItemName()).isEqualTo("A4コピー用紙 500枚");
            assertThat(updatedDetail.getSnapKind()).isEqualTo(ItemKind.GOODS);
            assertThat(updatedDetail.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222221");
            assertThat(updatedDetail.getSnapUnitName()).isEqualTo("個");
            assertThat(updatedDetail.getSupplierId()).isEqualTo("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            assertThat(updatedDetail.getSnapSupplierName()).isEqualTo("神奈川文具株式会社");
            assertThat(updatedDetail.getQuantity()).isEqualTo(2);
            assertThat(updatedDetail.getSnapUnitPrice()).isEqualTo(680);
            assertThat(updatedDetail.getSubtotalExcludingTax()).isEqualTo(1360);

            verify(purchaseRequestDetailMapper, never()).deleteByPrimaryKey(anyString());
        }

        @Test
        void update_upsert() {
            PurchaseRequestDetail detail = new PurchaseRequestDetail();

            detail.setPrDetailId("ebe68ea0-daaf-41ff-99e3-8ab109c20eae");
            detail.setPrId("6b2c5959-233f-4b54-8a9b-98f4a1b13c40");
            detail.setItemId(null);
            detail.setSnapItemName("会議室プロジェクター設置作業");
            detail.setSnapKind(ItemKind.SERVICE);
            detail.setUnitId("22222222-2222-2222-2222-222222222224");
            detail.setSnapUnitName("式");
            detail.setSupplierId("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
            detail.setSnapSupplierName("関西オフィスサービス株式会社");
            detail.setQuantity(2);
            detail.setSnapUnitPrice(25000);
            detail.setSubtotalExcludingTax(50000);
            detail.setCreatedAt(LocalDateTime.of(2026, 4, 26, 14, 40, 21, 434_000_000));
            detail.setUpdatedAt(LocalDateTime.of(2026, 5, 2, 19, 56, 0, 433_000_000));
            detail.setLineNo(2);
            purchaseRequestDetailMapper.updateByPrimaryKey(detail);

            // カタログ新規行
            PurchaseRequestDetailEditForm newCatalog = new PurchaseRequestDetailEditForm();
            newCatalog.setDetailInputType(DetailInputType.CATALOG);
            newCatalog.setQuantity(2);
            newCatalog.setItemId("1bd0d872-69b1-4999-b522-ac202c481662");
            newCatalog.setKind(ItemKind.GOODS);
            newCatalog.setItemName("ボールペン");
            newCatalog.setSupplierId("test");
            newCatalog.setSupplierName("test会社");
            newCatalog.setUnitId("22222222");
            newCatalog.setUnitName("わあ");
            newCatalog.setPrice(10000);

            // カタログ既存行
            PurchaseRequestDetailEditForm existingCatalog = new PurchaseRequestDetailEditForm();
            existingCatalog.setPrDetailId("4d57ee8a-4dc6-4155-9f4b-9ee7985d4e21");
            existingCatalog.setDetailInputType(DetailInputType.CATALOG);
            existingCatalog.setQuantity(1);
            existingCatalog.setItemId("2cc30fd9-9dae-4abe-a145-b280d9de2f38");
            existingCatalog.setKind(ItemKind.SERVICE);
            existingCatalog.setItemName("プリンター保守サポート");
            existingCatalog.setSupplierId("c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a");
            existingCatalog.setSupplierName("中部設備サプライ株式会社");
            existingCatalog.setUnitId("22222222-2222-2222-2222-222222222225");
            existingCatalog.setUnitName("時間");
            existingCatalog.setPrice(4500);

            // フリー入力既存行
            PurchaseRequestDetailEditForm existingFree = new PurchaseRequestDetailEditForm();
            existingFree.setPrDetailId("ebe68ea0-daaf-41ff-99e3-8ab109c20eae");
            existingFree.setDetailInputType(DetailInputType.FREE);
            existingFree.setQuantity(4);
            existingFree.setItemId(null);
            existingFree.setKind(ItemKind.GOODS);
            existingFree.setItemName("ぬいぐるみ");
            existingFree.setSupplierId(null);
            existingFree.setSupplierName("ぬいぐるみ株式会社");
            existingFree.setUnitId(null);
            existingFree.setUnitName("ぬいぐる");
            existingFree.setPrice(700);

            PurchaseRequestEditForm form = new PurchaseRequestEditForm();
            form.setDetails(List.of(newCatalog, existingCatalog, existingFree));

            purchaseRequestService.update("6b2c5959-233f-4b54-8a9b-98f4a1b13c40", form);

            PurchaseRequest updatedHeader = purchaseRequestMapper
                .selectByPrimaryKey("6b2c5959-233f-4b54-8a9b-98f4a1b13c40");
            assertThat(updatedHeader.getTotalAmountExcludingTax()).isEqualTo(9260);

            PurchaseRequestDetailExample ex = new PurchaseRequestDetailExample();
            ex.createCriteria().andPrIdEqualTo("6b2c5959-233f-4b54-8a9b-98f4a1b13c40");
            ex.setOrderByClause("line_no asc");
            List<PurchaseRequestDetail> actuals = purchaseRequestDetailMapper.selectByExample(ex);
            assertThat(actuals).hasSize(3);

            PurchaseRequestDetail first = actuals.get(0);
            assertThat(first.getLineNo()).isEqualTo(1);
            assertThat(first.getItemId()).isEqualTo("1bd0d872-69b1-4999-b522-ac202c481662");
            assertThat(first.getSnapItemName()).isEqualTo("油性ボールペン 黒 10本セット");
            assertThat(first.getQuantity()).isEqualTo(2);
            assertThat(first.getSubtotalExcludingTax()).isEqualTo(1960);

            PurchaseRequestDetail second = actuals.get(1);
            assertThat(second.getLineNo()).isEqualTo(2);
            assertThat(second.getQuantity()).isEqualTo(1);
            assertThat(second.getSubtotalExcludingTax()).isEqualTo(4500);

            PurchaseRequestDetail third = actuals.get(2);
            assertThat(third.getLineNo()).isEqualTo(3);
            assertThat(third.getSnapItemName()).isEqualTo("ぬいぐるみ");
            assertThat(third.getSnapKind()).isEqualTo(ItemKind.GOODS);
            assertThat(third.getQuantity()).isEqualTo(4);
            assertThat(third.getSubtotalExcludingTax()).isEqualTo(2800);

        }

        @Test
        void update_with_deleted() {
            PurchaseRequestEditForm form = new PurchaseRequestEditForm();
            form.setDeletedPrDetailIds(List.of("4d57ee8a-4dc6-4155-9f4b-9ee7985d4e21"));

            purchaseRequestService.update("6b2c5959-233f-4b54-8a9b-98f4a1b13c40", form);

            PurchaseRequestDetail actual = purchaseRequestDetailMapper
                .selectByPrimaryKey("4d57ee8a-4dc6-4155-9f4b-9ee7985d4e21");
            assertThat(actual).isNull();
        }

    }

}
