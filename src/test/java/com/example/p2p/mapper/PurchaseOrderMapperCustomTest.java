package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.app.PurchaseOrderCreateSourceDto;
import com.example.p2p.dto.app.PurchaseOrderCreateSourceRowDto;
import com.example.p2p.dto.app.PurchaseOrderDetailSelectionPrGroupDto;
import com.example.p2p.dto.app.PurchaseOrderDetailSelectionRowDto;
import com.example.p2p.dto.app.PurchaseOrderDetailSelectionViewDto;
import com.example.p2p.dto.app.PurchaseOrderDetailDto;
import com.example.p2p.dto.app.PurchaseOrderListRowDto;
import com.example.p2p.dto.app.RelatedPrDto;
import com.example.p2p.dto.app.PurchaseOrderSupplierSelectionRowDto;
import com.example.p2p.entity.PurchaseOrder;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.entity.PurchaseRequestDetail;
import com.example.p2p.entity.PurchaseRequestPurchaseOrder;
import com.example.p2p.entity.Supplier;
import com.example.p2p.entity.Users;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.PurchaseOrderSortBy;
import com.example.p2p.enums.PurchaseOrderStatus;
import com.example.p2p.enums.PurchaseOrderSupplierSelectionSortBy;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.app.PurchaseOrderSearchForm;
import com.example.p2p.form.app.PurchaseOrderSupplierSelectionSearchForm;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PurchaseOrderMapperCustomTest {

    @Autowired
    PurchaseOrderMapperCustom purchaseOrderMapperCustom;

    @Autowired
    PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    PurchaseRequestMapper purchaseRequestMapper;

    @Autowired
    PurchaseRequestPurchaseOrderMapper purchaseRequestPurchaseOrderMapper;

    @Autowired
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    @Autowired
    SupplierMapper supplierMapper;

    @Autowired
    UsersMapper usersMapper;

    @Nested
    class SelectPurchaseOrders {

        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";

        String firstPoId = "6f3b9242-2d1e-4e7a-b875-021c3f9a1a01";

        String targetDateSupplierId = "50000000-0000-4000-9000-000000000001";

        @Test
        void selectPurchaseOrders_noCondition() {
            List<PurchaseOrderListRowDto> actual = purchaseOrderMapperCustom
                .selectPurchaseOrders(new PurchaseOrderSearchForm(), VisibilityScope.ALL, userId);

            assertThat(actual).hasSize(2);

            PurchaseOrder expectedOrder = purchaseOrderMapper.selectByPrimaryKey(firstPoId);
            Users purchaser = usersMapper.selectByPrimaryKey(expectedOrder.getPurchaserUserId());
            String expectedPurchaser = purchaser.getLastName() + " " + purchaser.getFirstName();

            PurchaseOrderListRowDto first = actual.get(0);
            assertThat(first.getPoId()).isEqualTo(firstPoId);
            assertThat(first.getDisplayNumber()).isOne();
            assertThat(first.getOrderType()).isEqualTo(PurchaseOrderType.STANDARD);
            assertThat(first.getPurchaser()).isEqualTo(expectedPurchaser);
            assertThat(first.getDeliveryDueDate()).isEqualTo(LocalDate.of(2026, 6, 25));
            assertThat(first.getSupplierName()).isEqualTo(expectedOrder.getSnapSupplierName());
            assertThat(first.getTotalAmountExcludingTax()).isEqualTo(6800);
            assertThat(first.getStatus()).isEqualTo(PurchaseOrderStatus.PENDING);
        }

        @ParameterizedTest
        @MethodSource("supplierKeywordCases")
        void selectPurchaseOrders_bySupplierKeyword(String sourcePoId, int displayNumber) {
            PurchaseOrder sourceOrder = purchaseOrderMapper.selectByPrimaryKey(sourcePoId);
            assertThat(sourceOrder).isNotNull();

            PurchaseOrderSearchForm form = new PurchaseOrderSearchForm();
            form.setKeyword(sourceOrder.getSnapSupplierName());

            List<PurchaseOrderListRowDto> actual = purchaseOrderMapperCustom.selectPurchaseOrders(form,
                    VisibilityScope.ALL, userId);
            assertThat(actual).hasSize(2);
            assertThat(actual.get(0).getDisplayNumber()).isEqualTo(displayNumber);
        }

        static Stream<Arguments> supplierKeywordCases() {
            return Stream.of(Arguments.of("d8b3f0c1-5a64-447a-8b29-2d88b8e2f005", 6),
                    Arguments.of("6f3b9242-2d1e-4e7a-b875-021c3f9a1a01", 1));
        }

        @Test
        void selectPurchaseOrders_targetDateFrom_orderTypeNull_boundary() {
            insertTargetDateFromFixture();
            PurchaseOrderSearchForm form = completedSearchForm();
            form.setTargetDateFrom(LocalDate.of(2026, 7, 10));

            assertDisplayNumbers(form, 10101, 10103);
        }

        @Test
        void selectPurchaseOrders_targetDateFrom_standard_boundary() {
            insertTargetDateFromFixture();
            PurchaseOrderSearchForm form = completedSearchForm(PurchaseOrderType.STANDARD);
            form.setTargetDateFrom(LocalDate.of(2026, 7, 10));

            assertDisplayNumbers(form, 10101);
        }

        @Test
        void selectPurchaseOrders_targetDateFrom_service_boundary() {
            insertTargetDateFromFixture();
            PurchaseOrderSearchForm form = completedSearchForm(PurchaseOrderType.SERVICE);
            form.setTargetDateFrom(LocalDate.of(2026, 7, 10));

            assertDisplayNumbers(form, 10103);
        }

        @Test
        void selectPurchaseOrders_targetDateTo_orderTypeNull_boundary() {
            insertTargetDateToFixture();
            PurchaseOrderSearchForm form = completedSearchForm();
            form.setTargetDateTo(LocalDate.of(2026, 7, 10));

            assertDisplayNumbers(form, 10201, 10203);
        }

        @Test
        void selectPurchaseOrders_targetDateTo_standard_boundary() {
            insertTargetDateToFixture();
            PurchaseOrderSearchForm form = completedSearchForm(PurchaseOrderType.STANDARD);
            form.setTargetDateTo(LocalDate.of(2026, 7, 10));

            assertDisplayNumbers(form, 10201);
        }

        @Test
        void selectPurchaseOrders_targetDateTo_service_boundary() {
            insertTargetDateToFixture();
            PurchaseOrderSearchForm form = completedSearchForm(PurchaseOrderType.SERVICE);
            form.setTargetDateTo(LocalDate.of(2026, 7, 10));

            assertDisplayNumbers(form, 10203);
        }

        @Test
        void selectPurchaseOrders_targetDateRange_service_overlapsBoundary() {
            insertServiceTargetDateRangeFixture();
            PurchaseOrderSearchForm form = completedSearchForm(PurchaseOrderType.SERVICE);
            form.setTargetDateFrom(LocalDate.of(2026, 7, 10));
            form.setTargetDateTo(LocalDate.of(2026, 7, 20));

            assertDisplayNumbers(form, 10301, 10302, 10303);
        }

        @ParameterizedTest
        @MethodSource("createSortCases")
        void selectPurchaseOrders_sortBy(PurchaseOrderSortBy sortBy, SortDirection direction, int firstDispNum,
                int secondDispNum) {
            PurchaseOrderSearchForm form = new PurchaseOrderSearchForm();
            form.setSortBy(sortBy);
            form.setSortDirection(direction);

            List<PurchaseOrderListRowDto> actual = purchaseOrderMapperCustom.selectPurchaseOrders(form,
                    VisibilityScope.ALL, userId);
            assertThat(actual).extracting(PurchaseOrderListRowDto::getDisplayNumber)
                .containsExactly(firstDispNum, secondDispNum);

        }

        static Stream<Arguments> createSortCases() {
            return Stream.of(Arguments.of(PurchaseOrderSortBy.NUMBER, SortDirection.ASC, 1, 2),
                    Arguments.of(PurchaseOrderSortBy.TYPE, SortDirection.DESC, 9, 8),
                    Arguments.of(PurchaseOrderSortBy.PURCHASER, SortDirection.ASC, 1, 5),
                    Arguments.of(PurchaseOrderSortBy.SUPPLIER, SortDirection.DESC, 8, 6));
        }

        @Test
        void selectPurchaseOrders_sortByTargetDate_orderTypeNull_asc() {
            insertTargetDateSortAscFixture();
            PurchaseOrderSearchForm form = completedSearchForm();
            form.setSortBy(PurchaseOrderSortBy.TARGET_DATE);
            form.setSortDirection(SortDirection.ASC);

            assertDisplayNumbers(form, 10401, 10402, 10405, 10403, 10404);
        }

        @Test
        void selectPurchaseOrders_sortByTargetDate_orderTypeNull_desc() {
            insertTargetDateSortDescFixture();
            PurchaseOrderSearchForm form = completedSearchForm();
            form.setSortBy(PurchaseOrderSortBy.TARGET_DATE);
            form.setSortDirection(SortDirection.DESC);

            assertDisplayNumbers(form, 10405, 10402, 10404, 10403, 10401);
        }

        @Test
        void selectPurchaseOrders_sortByTargetDate_standard_deliveryDueDateAsc() {
            insertTargetDateSortAscFixture();
            PurchaseOrderSearchForm form = completedSearchForm(PurchaseOrderType.STANDARD);
            form.setSortBy(PurchaseOrderSortBy.TARGET_DATE);
            form.setSortDirection(SortDirection.ASC);

            assertDisplayNumbers(form, 10401, 10403);
        }

        @Test
        void selectPurchaseOrders_sortByTargetDate_service_periodFromAsc() {
            insertTargetDateSortAscFixture();
            PurchaseOrderSearchForm form = completedSearchForm(PurchaseOrderType.SERVICE);
            form.setSortBy(PurchaseOrderSortBy.TARGET_DATE);
            form.setSortDirection(SortDirection.ASC);

            assertDisplayNumbers(form, 10402, 10405, 10404);
        }

        @Test
        void selectPurchaseOrders_sortByTargetDate_service_periodToAndPeriodFromDesc() {
            insertTargetDateSortDescFixture();
            PurchaseOrderSearchForm form = completedSearchForm(PurchaseOrderType.SERVICE);
            form.setSortBy(PurchaseOrderSortBy.TARGET_DATE);
            form.setSortDirection(SortDirection.DESC);

            assertDisplayNumbers(form, 10405, 10402, 10404);
        }

        private void insertTargetDateFromFixture() {
            insertTargetDateTestSupplier();
            insertStandardPurchaseOrder(10101, LocalDate.of(2026, 7, 10), PurchaseOrderStatus.COMPLETED);
            insertStandardPurchaseOrder(10102, LocalDate.of(2026, 7, 9), PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10103, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 10),
                    PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10104, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 9),
                    PurchaseOrderStatus.COMPLETED);
        }

        private void insertTargetDateToFixture() {
            insertTargetDateTestSupplier();
            insertStandardPurchaseOrder(10201, LocalDate.of(2026, 7, 10), PurchaseOrderStatus.COMPLETED);
            insertStandardPurchaseOrder(10202, LocalDate.of(2026, 7, 11), PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10203, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 20),
                    PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10204, LocalDate.of(2026, 7, 11), LocalDate.of(2026, 7, 20),
                    PurchaseOrderStatus.COMPLETED);
        }

        private void insertServiceTargetDateRangeFixture() {
            insertTargetDateTestSupplier();
            insertServicePurchaseOrder(10301, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 10),
                    PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10302, LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 31),
                    PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10303, LocalDate.of(2026, 7, 12), LocalDate.of(2026, 7, 15),
                    PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10304, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 9),
                    PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10305, LocalDate.of(2026, 7, 21), LocalDate.of(2026, 7, 31),
                    PurchaseOrderStatus.COMPLETED);
            insertStandardPurchaseOrder(10306, LocalDate.of(2026, 7, 15), PurchaseOrderStatus.COMPLETED);
        }

        private void insertTargetDateSortAscFixture() {
            insertTargetDateTestSupplier();
            insertStandardPurchaseOrder(10401, LocalDate.of(2026, 7, 10), PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10402, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 20),
                    PurchaseOrderStatus.COMPLETED);
            insertStandardPurchaseOrder(10403, LocalDate.of(2026, 7, 20), PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10404, LocalDate.of(2026, 7, 25), LocalDate.of(2026, 7, 26),
                    PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10405, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 30),
                    PurchaseOrderStatus.COMPLETED);
            insertStandardPurchaseOrder(10406, LocalDate.of(2026, 7, 1), PurchaseOrderStatus.PENDING);
        }

        private void insertTargetDateSortDescFixture() {
            insertTargetDateTestSupplier();
            insertStandardPurchaseOrder(10401, LocalDate.of(2026, 7, 10), PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10402, LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 30),
                    PurchaseOrderStatus.COMPLETED);
            insertStandardPurchaseOrder(10403, LocalDate.of(2026, 7, 20), PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10404, LocalDate.of(2026, 7, 25), LocalDate.of(2026, 7, 26),
                    PurchaseOrderStatus.COMPLETED);
            insertServicePurchaseOrder(10405, LocalDate.of(2026, 7, 15), LocalDate.of(2026, 7, 30),
                    PurchaseOrderStatus.COMPLETED);
            insertStandardPurchaseOrder(10406, LocalDate.of(2026, 7, 1), PurchaseOrderStatus.PENDING);
        }

        private PurchaseOrderSearchForm completedSearchForm() {
            return completedSearchForm(null);
        }

        private PurchaseOrderSearchForm completedSearchForm(PurchaseOrderType orderType) {
            PurchaseOrderSearchForm form = new PurchaseOrderSearchForm();
            form.setOrderType(orderType);
            form.setStatus(PurchaseOrderStatus.COMPLETED);
            form.setSupplierId(targetDateSupplierId);
            form.setSize(100);
            return form;
        }

        private void assertDisplayNumbers(PurchaseOrderSearchForm form, Integer... expectedDisplayNumbers) {
            List<PurchaseOrderListRowDto> actual = purchaseOrderMapperCustom.selectPurchaseOrders(form,
                    VisibilityScope.ALL, userId);

            assertThat(actual).extracting(PurchaseOrderListRowDto::getDisplayNumber)
                .containsExactly(expectedDisplayNumbers);
            assertThat(purchaseOrderMapperCustom.countPurchaseOrders(form, VisibilityScope.ALL, userId))
                .isEqualTo(expectedDisplayNumbers.length);
        }

        private void insertStandardPurchaseOrder(int displayNumber, LocalDate deliveryDueDate,
                PurchaseOrderStatus status) {
            PurchaseOrder order = basePurchaseOrder(displayNumber, PurchaseOrderType.STANDARD, status);
            order.setDeliveryDueDate(deliveryDueDate);
            purchaseOrderMapper.insertSelective(order);
        }

        private void insertServicePurchaseOrder(int displayNumber, LocalDate servicePeriodFrom,
                LocalDate servicePeriodTo, PurchaseOrderStatus status) {
            PurchaseOrder order = basePurchaseOrder(displayNumber, PurchaseOrderType.SERVICE, status);
            order.setServicePeriodFrom(servicePeriodFrom);
            order.setServicePeriodTo(servicePeriodTo);
            purchaseOrderMapper.insertSelective(order);
        }

        private PurchaseOrder basePurchaseOrder(int displayNumber, PurchaseOrderType orderType,
                PurchaseOrderStatus status) {
            PurchaseOrder order = new PurchaseOrder();
            order.setPoId("40000000-0000-4000-9000-" + String.format("%012d", displayNumber));
            order.setDisplayNumber(displayNumber);
            order.setOrderType(orderType);
            order.setPurchaserUserId(userId);
            order.setSupplierId(targetDateSupplierId);
            order.setSnapSupplierName("Target Date Test Supplier");
            order.setTotalAmountExcludingTax(1000);
            order.setStatus(status);
            order.setCurrentStepOrder(1);
            return order;
        }

        private void insertTargetDateTestSupplier() {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(targetDateSupplierId);
            supplier.setName("Target Date Test Supplier");
            supplier.setNameKana("Target Date Test Supplier");
            supplierMapper.insertSelective(supplier);
        }

    }

    @Nested
    class SelectPurchaseOrderDetailHeader {

        String standardPoId = "6f3b9242-2d1e-4e7a-b875-021c3f9a1a01";

        String servicePoId = "3d062413-54a0-40f0-b764-3d1c6e4f5a02";

        String standardPurchaserUserId = "169f1e17-619f-45bf-b6dc-8faed08c404c";

        String servicePurchaserUserId = "36a1d5d9-15b8-45d5-8ae7-607244bbe36e";

        String otherPrId = "3a5b130e-0279-4bca-bee3-d41cddbc9192";

        @Test
        void selectPurchaseOrderDetailHeader_all_standardPo() {
            PurchaseRequestPurchaseOrder relation = new PurchaseRequestPurchaseOrder();
            relation.setPrId(otherPrId);
            relation.setPoId(standardPoId);
            purchaseRequestPurchaseOrderMapper.insertSelective(relation);

            LocalDateTime createdAt = LocalDateTime.of(2026, 6, 15, 10, 30);
            PurchaseOrder order = new PurchaseOrder();
            order.setPoId(standardPoId);
            order.setCreatedAt(createdAt);
            purchaseOrderMapper.updateByPrimaryKeySelective(order);

            PurchaseOrder expectedOrder = purchaseOrderMapper.selectByPrimaryKey(standardPoId);
            Users purchaser = usersMapper.selectByPrimaryKey(standardPurchaserUserId);
            String expectedPurchaser = purchaser.getLastName() + " " + purchaser.getFirstName();

            PurchaseOrderDetailDto actual = purchaseOrderMapperCustom.selectPurchaseOrderDetailHeader(standardPoId,
                    VisibilityScope.ALL, servicePurchaserUserId);

            assertThat(actual.getPoId()).isEqualTo(standardPoId);
            assertThat(actual.getDisplayNumber()).isEqualTo(expectedOrder.getDisplayNumber());
            assertThat(actual.getOrderType()).isEqualTo(expectedOrder.getOrderType());
            assertThat(actual.getSupplierName()).isEqualTo(expectedOrder.getSnapSupplierName());
            assertThat(actual.getUserId()).isEqualTo(standardPurchaserUserId);
            assertThat(actual.getPurchaser()).isEqualTo(expectedPurchaser);
            assertThat(actual.getTotalAmountExcludingTax()).isEqualTo(expectedOrder.getTotalAmountExcludingTax());
            assertThat(actual.getStatus()).isEqualTo(expectedOrder.getStatus());
            assertThat(actual.getNote()).isEqualTo(expectedOrder.getNote());
            assertThat(actual.getCreatedAt()).isEqualTo(createdAt.toLocalDate());
            assertThat(actual.getDeliveryDueDate()).isEqualTo(expectedOrder.getDeliveryDueDate());
            assertThat(actual.getServicePeriodFrom()).isNull();
            assertThat(actual.getServicePeriodTo()).isNull();
            assertThat(actual.getRelatedPrs())
                .extracting(RelatedPrDto::getPrId, RelatedPrDto::getDisplayNumber)
                .containsExactly(tuple("88bfbcf6-2be6-4d31-8a46-155a7b58ab93", 1),
                        tuple("3a5b130e-0279-4bca-bee3-d41cddbc9192", 2));
        }

        @Test
        void selectPurchaseOrderDetailHeader_all_serviceOrder() {
            LocalDateTime createdAt = LocalDateTime.of(2026, 6, 16, 9, 15);
            PurchaseOrder order = new PurchaseOrder();
            order.setPoId(servicePoId);
            order.setCreatedAt(createdAt);
            purchaseOrderMapper.updateByPrimaryKeySelective(order);

            PurchaseOrder expectedOrder = purchaseOrderMapper.selectByPrimaryKey(servicePoId);
            Users purchaser = usersMapper.selectByPrimaryKey(servicePurchaserUserId);
            String expectedPurchaser = purchaser.getLastName() + " " + purchaser.getFirstName();

            PurchaseOrderDetailDto actual = purchaseOrderMapperCustom.selectPurchaseOrderDetailHeader(servicePoId,
                    VisibilityScope.ALL, standardPurchaserUserId);

            assertThat(actual.getPoId()).isEqualTo(servicePoId);
            assertThat(actual.getDisplayNumber()).isEqualTo(expectedOrder.getDisplayNumber());
            assertThat(actual.getOrderType()).isEqualTo(expectedOrder.getOrderType());
            assertThat(actual.getSupplierName()).isEqualTo(expectedOrder.getSnapSupplierName());
            assertThat(actual.getUserId()).isEqualTo(servicePurchaserUserId);
            assertThat(actual.getPurchaser()).isEqualTo(expectedPurchaser);
            assertThat(actual.getTotalAmountExcludingTax()).isEqualTo(expectedOrder.getTotalAmountExcludingTax());
            assertThat(actual.getStatus()).isEqualTo(expectedOrder.getStatus());
            assertThat(actual.getNote()).isEqualTo(expectedOrder.getNote());
            assertThat(actual.getCreatedAt()).isEqualTo(createdAt.toLocalDate());
            assertThat(actual.getDeliveryDueDate()).isNull();
            assertThat(actual.getServicePeriodFrom()).isEqualTo(expectedOrder.getServicePeriodFrom());
            assertThat(actual.getServicePeriodTo()).isEqualTo(expectedOrder.getServicePeriodTo());
            assertThat(actual.getRelatedPrs())
                .extracting(RelatedPrDto::getPrId, RelatedPrDto::getDisplayNumber)
                .containsExactly(tuple("3a5b130e-0279-4bca-bee3-d41cddbc9192", 2));
        }

        @Test
        void selectPurchaseOrderDetailHeader_self() {
            PurchaseOrderDetailDto actual = purchaseOrderMapperCustom.selectPurchaseOrderDetailHeader(standardPoId,
                    VisibilityScope.SELF, standardPurchaserUserId);

            assertThat(actual.getUserId()).isEqualTo(standardPurchaserUserId);
        }

    }

    @Nested
    class SelectPurchaseOrderSupplierSelections {

        String freeInputPrId = "56856dfe-8e7a-4524-9d05-9e161c6b8fc3";

        String firstCatalogPrId = "88bfbcf6-2be6-4d31-8a46-155a7b58ab93";

        String firstCatalogSupplierId = "a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c";

        String catalogPrId = "3c4f62bf-855b-4c35-b19d-eb06acb16896";

        String catalogSupplierId = "b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f";

        String freeInputSupplierName = "Free Input Supplier";

        @Test
        void selectPurchaseOrderSupplierSelections_supplierIdNull_freeInputAndCatalog() {
            approvePurchaseRequests(freeInputPrId, firstCatalogPrId, catalogPrId);
            int freeInputSubtotal = insertFreeInputGoodsDetail();
            insertNonApprovedGoodsDetails();
            Supplier firstCatalogSupplier = supplierMapper.selectByPrimaryKey(firstCatalogSupplierId);
            Supplier catalogSupplier = supplierMapper.selectByPrimaryKey(catalogSupplierId);

            List<PurchaseOrderSupplierSelectionRowDto> actual = purchaseOrderMapperCustom
                .selectPurchaseOrderSupplierSelections(ItemKind.GOODS, form(null));

            assertThat(actual).hasSize(3);
            assertThat(actual).extracting(PurchaseOrderSupplierSelectionRowDto::getSupplierId)
                .containsExactly(null, firstCatalogSupplierId, catalogSupplierId);

            PurchaseOrderSupplierSelectionRowDto first = actual.get(0);
            assertThat(first.getSupplierId()).isNull();
            assertThat(first.getSupplierName()).isEqualTo(freeInputSupplierName);
            assertThat(first.getDetailCount()).isOne();
            assertThat(first.getPurchaseRequests())
                .extracting(RelatedPrDto::getPrId, RelatedPrDto::getDisplayNumber)
                .containsExactly(tuple(freeInputPrId, 4));
            assertThat(first.getTotalAmountExcludingTax()).isEqualTo(freeInputSubtotal);

            PurchaseOrderSupplierSelectionRowDto second = actual.get(1);
            assertThat(second.getSupplierId()).isEqualTo(firstCatalogSupplierId);
            assertThat(second.getSupplierName()).isEqualTo(firstCatalogSupplier.getName());
            assertThat(second.getDetailCount()).isEqualTo(3);
            assertThat(second.getPurchaseRequests())
                .extracting(RelatedPrDto::getPrId, RelatedPrDto::getDisplayNumber)
                .containsExactly(tuple(firstCatalogPrId, 1), tuple(catalogPrId, 3));
            assertThat(second.getTotalAmountExcludingTax()).isEqualTo(13140);

            PurchaseOrderSupplierSelectionRowDto third = actual.get(2);
            assertThat(third.getSupplierId()).isEqualTo(catalogSupplierId);
            assertThat(third.getSupplierName()).isEqualTo(catalogSupplier.getName());
            assertThat(third.getDetailCount()).isEqualTo(2);
            assertThat(third.getPurchaseRequests())
                .extracting(RelatedPrDto::getPrId, RelatedPrDto::getDisplayNumber)
                .containsExactlyInAnyOrder(tuple(catalogPrId, 3), tuple(freeInputPrId, 4));
            assertThat(third.getTotalAmountExcludingTax()).isEqualTo(50400);
        }

        @Test
        void selectPurchaseOrderSupplierSelections_supplierIdSpecified_catalogOnly() {
            approvePurchaseRequests(freeInputPrId, firstCatalogPrId, catalogPrId);
            insertFreeInputGoodsDetail();
            insertNonApprovedGoodsDetails();
            Supplier supplier = supplierMapper.selectByPrimaryKey(firstCatalogSupplierId);

            List<PurchaseOrderSupplierSelectionRowDto> actual = purchaseOrderMapperCustom
                .selectPurchaseOrderSupplierSelections(ItemKind.GOODS, form(firstCatalogSupplierId));

            assertThat(actual).hasSize(1);

            PurchaseOrderSupplierSelectionRowDto selected = actual.get(0);
            assertThat(selected.getSupplierId()).isEqualTo(firstCatalogSupplierId);
            assertThat(selected.getSupplierName()).isEqualTo(supplier.getName());
            assertThat(selected.getDetailCount()).isEqualTo(3);
            assertThat(selected.getPurchaseRequests())
                .extracting(RelatedPrDto::getPrId, RelatedPrDto::getDisplayNumber)
                .containsExactly(tuple(firstCatalogPrId, 1), tuple(catalogPrId, 3));
            assertThat(selected.getTotalAmountExcludingTax()).isEqualTo(13140);
        }

        @Nested
        class Sort {

            @Test
            void selectPurchaseOrderSupplierSelections_sortBySupplier_desc() {
                assertSorted(PurchaseOrderSupplierSelectionSortBy.SUPPLIER, SortDirection.DESC,
                        Arrays.asList(catalogSupplierId, firstCatalogSupplierId, null));
            }

            @Test
            void selectPurchaseOrderSupplierSelections_sortByDetailCount_asc() {
                assertSorted(PurchaseOrderSupplierSelectionSortBy.DETAIL_COUNT, SortDirection.ASC,
                        Arrays.asList(null, catalogSupplierId, firstCatalogSupplierId));
            }

            @Test
            void selectPurchaseOrderSupplierSelections_sortByTotalAmount_desc() {
                assertSorted(PurchaseOrderSupplierSelectionSortBy.TOTAL_AMOUNT, SortDirection.DESC,
                        Arrays.asList(catalogSupplierId, firstCatalogSupplierId, null));
            }

            private void assertSorted(PurchaseOrderSupplierSelectionSortBy sortBy, SortDirection sortDirection,
                    List<String> expectedSupplierIds) {
                approvePurchaseRequests(freeInputPrId, firstCatalogPrId, catalogPrId);
                insertFreeInputGoodsDetail();
                insertNonApprovedGoodsDetails();

                List<PurchaseOrderSupplierSelectionRowDto> actual = purchaseOrderMapperCustom
                    .selectPurchaseOrderSupplierSelections(ItemKind.GOODS, form(null, sortBy, sortDirection));

                assertThat(actual).extracting(PurchaseOrderSupplierSelectionRowDto::getSupplierId)
                    .containsExactlyElementsOf(expectedSupplierIds);
            }

        }

        private PurchaseOrderSupplierSelectionSearchForm form(String supplierId) {
            return form(supplierId, PurchaseOrderSupplierSelectionSortBy.SUPPLIER, SortDirection.ASC);
        }

        private PurchaseOrderSupplierSelectionSearchForm form(String supplierId,
                PurchaseOrderSupplierSelectionSortBy sortBy, SortDirection sortDirection) {
            PurchaseOrderSupplierSelectionSearchForm form = new PurchaseOrderSupplierSelectionSearchForm();
            form.setSupplierId(supplierId);
            form.setSortBy(sortBy);
            form.setSortDirection(sortDirection);
            return form;
        }

        private void approvePurchaseRequests(String... prIds) {
            for (String prId : prIds) {
                PurchaseRequest request = new PurchaseRequest();
                request.setPrId(prId);
                request.setStatus(PurchaseRequestStatus.APPROVED);
                purchaseRequestMapper.updateByPrimaryKeySelective(request);
            }
        }

        private void insertNonApprovedGoodsDetails() {
            Supplier firstCatalogSupplier = supplierMapper.selectByPrimaryKey(firstCatalogSupplierId);
            Supplier catalogSupplier = supplierMapper.selectByPrimaryKey(catalogSupplierId);

            insertPurchaseRequest("30000000-0000-4000-9000-000000000101", PurchaseRequestStatus.PENDING, 5_000_000);
            for (int i = 1; i <= 5; i++) {
                insertPurchaseRequestDetail("30000000-0000-4000-9000-00000000010" + i,
                        "30000000-0000-4000-9000-000000000101", i, null, freeInputSupplierName,
                        "Pending Free Input Item " + i, 1_000_000);
            }

            insertPurchaseRequest("30000000-0000-4000-9000-000000000201", PurchaseRequestStatus.REJECTED, 900_000);
            insertPurchaseRequestDetail("30000000-0000-4000-9000-000000000202",
                    "30000000-0000-4000-9000-000000000201", 1, firstCatalogSupplierId, firstCatalogSupplier.getName(),
                    "Rejected Catalog Item", 900_000);

            insertPurchaseRequest("30000000-0000-4000-9000-000000000301", PurchaseRequestStatus.CANCELLED, 800_000);
            insertPurchaseRequestDetail("30000000-0000-4000-9000-000000000302",
                    "30000000-0000-4000-9000-000000000301", 1, catalogSupplierId, catalogSupplier.getName(),
                    "Cancelled Catalog Item", 800_000);
        }

        private void insertPurchaseRequest(String prId, PurchaseRequestStatus status, int totalAmountExcludingTax) {
            PurchaseRequest request = new PurchaseRequest();
            request.setPrId(prId);
            request.setRequesterUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
            request.setDueDate(LocalDate.of(2026, 7, 7));
            request.setTotalAmountExcludingTax(totalAmountExcludingTax);
            request.setStatus(status);
            request.setCurrentStepOrder(1);
            purchaseRequestMapper.insertSelective(request);
        }

        private void insertPurchaseRequestDetail(String prDetailId, String prId, int lineNo, String supplierId,
                String supplierName, String itemName, int subtotalExcludingTax) {
            PurchaseRequestDetail detail = new PurchaseRequestDetail();
            detail.setPrDetailId(prDetailId);
            detail.setPrId(prId);
            detail.setLineNo(lineNo);
            detail.setSnapKind(ItemKind.GOODS);
            detail.setSupplierId(supplierId);
            detail.setSnapSupplierName(supplierName);
            detail.setSnapItemName(itemName);
            detail.setSnapUnitName("piece");
            detail.setSnapUnitPrice(subtotalExcludingTax);
            detail.setQuantity(1);
            detail.setSubtotalExcludingTax(subtotalExcludingTax);
            purchaseRequestDetailMapper.insertSelective(detail);
        }

        private int insertFreeInputGoodsDetail() {
            int freeInputSubtotal = 2468;

            PurchaseRequestDetail freeInput = new PurchaseRequestDetail();
            freeInput.setPrId(freeInputPrId);
            freeInput.setSnapItemName("Free Input Item");
            freeInput.setSnapKind(ItemKind.GOODS);
            freeInput.setSnapUnitName("piece");
            freeInput.setSnapSupplierName(freeInputSupplierName);
            freeInput.setQuantity(2);
            freeInput.setSnapUnitPrice(1234);
            freeInput.setSubtotalExcludingTax(freeInputSubtotal);
            freeInput.setLineNo(3);
            purchaseRequestDetailMapper.insertSelective(freeInput);

            return freeInputSubtotal;
        }

    }

    @Nested
    class SelectPurchaseOrderDetailSelectionView {

        @Test
        void selectPurchaseOrderDetailSelectionView_supplierIdSpecified_goodsApprovedOnly() {
            GoodsSelectionFixture fixture = insertGoodsSelectionFixture();

            PurchaseOrderDetailSelectionViewDto actual = purchaseOrderMapperCustom
                .selectPurchaseOrderDetailSelectionView(fixture.supplierId, null, ItemKind.GOODS);

            PurchaseOrderDetailSelectionViewDto view = actual;
            assertThat(view.getTotalAmountExcludingTax()).isEqualTo(fixture.totalAmountExcludingTax);
            assertThat(view.getPrGroups()).hasSize(3);
            assertThat(view.getPrGroups())
                .extracting(PurchaseOrderDetailSelectionPrGroupDto::getDisplayNumber)
                .containsExactly(fixture.threeDetailPr.getDisplayNumber(), fixture.oneDetailPr1.getDisplayNumber(),
                        fixture.oneDetailPr2.getDisplayNumber());

            PurchaseOrderDetailSelectionPrGroupDto firstGroup = view.getPrGroups().get(0);
            assertThat(firstGroup.getPrId()).isEqualTo(fixture.threeDetailPr.getPrId());
            assertThat(firstGroup.getPrTotalAmountExcludingTax()).isEqualTo(fixture.threeDetailPrTotalAmountExcludingTax);
            assertThat(firstGroup.getDetailRows()).hasSize(3);
            assertThat(firstGroup.getDetailRows())
                .extracting(PurchaseOrderDetailSelectionRowDto::getPrDetailId)
                .containsExactly(fixture.firstDetailId, fixture.secondDetailId, fixture.thirdDetailId);
            assertThat(view.getPrGroups().stream()
                .flatMap(group -> group.getDetailRows().stream())
                .map(PurchaseOrderDetailSelectionRowDto::getDetailIndex))
                .containsExactly(0, 1, 2, 3, 4);

            PurchaseOrderDetailSelectionRowDto first = firstGroup.getDetailRows().get(0);
            assertThat(first.getPrDetailId()).isEqualTo(fixture.firstDetailId);
            assertThat(first.getDetailIndex()).isZero();
            assertThat(first.getItemName()).isEqualTo(fixture.firstItemName);
            assertThat(first.getUnitName()).isEqualTo(fixture.firstUnitName);
            assertThat(first.getUnitPrice()).isEqualTo(fixture.firstUnitPrice);
            assertThat(first.getQuantity()).isEqualTo(fixture.firstQuantity);
            assertThat(first.getSubtotalExcludingTax()).isEqualTo(fixture.firstSubtotalExcludingTax);
        }

        @Test
        void selectPurchaseOrderDetailSelectionView_supplierIdNull_serviceFreeInputOnly() {
            ServiceSelectionFixture fixture = insertServiceSelectionFixture();

            PurchaseOrderDetailSelectionViewDto actual = purchaseOrderMapperCustom
                .selectPurchaseOrderDetailSelectionView(null, fixture.freeInputSupplierName, ItemKind.SERVICE);

            assertThat(actual.getPrGroups()).hasSize(1);
            assertThat(actual.getPrGroups().get(0).getDetailRows()).hasSize(1);
            assertThat(actual.getPrGroups().get(0).getDetailRows().get(0).getDetailIndex()).isZero();
        }

        private GoodsSelectionFixture insertGoodsSelectionFixture() {
            GoodsSelectionFixture fixture = new GoodsSelectionFixture();
            Supplier supplier = supplierMapper.selectByPrimaryKey(fixture.supplierId);

            fixture.threeDetailPr = insertPurchaseRequest("10000000-0000-4000-9000-000000000101",
                    PurchaseRequestStatus.APPROVED, 5120);
            fixture.threeDetailPrTotalAmountExcludingTax += insertPurchaseRequestDetail(fixture.firstDetailId,
                    fixture.threeDetailPr.getPrId(), 1, ItemKind.GOODS, fixture.supplierId, supplier.getName(),
                    fixture.firstItemName, fixture.firstUnitName, fixture.firstUnitPrice, fixture.firstQuantity);
            fixture.threeDetailPrTotalAmountExcludingTax += insertPurchaseRequestDetail(fixture.secondDetailId,
                    fixture.threeDetailPr.getPrId(), 2,
                    ItemKind.GOODS, fixture.supplierId, supplier.getName(), "Goods approved three detail 2", "box",
                    2000, 1);
            fixture.threeDetailPrTotalAmountExcludingTax += insertPurchaseRequestDetail(fixture.thirdDetailId,
                    fixture.threeDetailPr.getPrId(), 3,
                    ItemKind.GOODS, fixture.supplierId, supplier.getName(), "Goods approved three detail 3", "set", 300,
                    3);
            fixture.totalAmountExcludingTax += fixture.threeDetailPrTotalAmountExcludingTax;

            fixture.oneDetailPr1 = insertPurchaseRequest("10000000-0000-4000-9000-000000000201",
                    PurchaseRequestStatus.APPROVED, 777);
            fixture.totalAmountExcludingTax += insertPurchaseRequestDetail("10000000-0000-4000-9000-000000000202",
                    fixture.oneDetailPr1.getPrId(), 1,
                    ItemKind.GOODS, fixture.supplierId, supplier.getName(), "Goods approved one detail 1", "piece", 777,
                    1);

            fixture.oneDetailPr2 = insertPurchaseRequest("10000000-0000-4000-9000-000000000301",
                    PurchaseRequestStatus.APPROVED, 1776);
            fixture.totalAmountExcludingTax += insertPurchaseRequestDetail("10000000-0000-4000-9000-000000000302",
                    fixture.oneDetailPr2.getPrId(), 1,
                    ItemKind.GOODS, fixture.supplierId, supplier.getName(), "Goods approved one detail 2", "piece", 888,
                    2);

            PurchaseRequest cancelled = insertPurchaseRequest("10000000-0000-4000-9000-000000000401",
                    PurchaseRequestStatus.CANCELLED, 9999);
            insertPurchaseRequestDetail("10000000-0000-4000-9000-000000000402", cancelled.getPrId(), 1, ItemKind.GOODS,
                    fixture.supplierId, supplier.getName(), "Goods cancelled detail", "piece", 9999, 1);

            return fixture;
        }

        private ServiceSelectionFixture insertServiceSelectionFixture() {
            ServiceSelectionFixture fixture = new ServiceSelectionFixture();
            Supplier supplier = supplierMapper.selectByPrimaryKey(fixture.supplierId);

            PurchaseRequest freeInput = insertPurchaseRequest("20000000-0000-4000-9000-000000000101",
                    PurchaseRequestStatus.APPROVED, 12000);
            insertPurchaseRequestDetail("20000000-0000-4000-9000-000000000102", freeInput.getPrId(), 1,
                    ItemKind.SERVICE, null, fixture.freeInputSupplierName, "Manual service detail", "hour", 12000, 1);

            PurchaseRequest supplierSpecified = insertPurchaseRequest("20000000-0000-4000-9000-000000000201",
                    PurchaseRequestStatus.APPROVED, 34000);
            insertPurchaseRequestDetail("20000000-0000-4000-9000-000000000202", supplierSpecified.getPrId(), 1,
                    ItemKind.SERVICE, fixture.supplierId, supplier.getName(), "Supplier service detail", "hour", 34000,
                    1);

            return fixture;
        }

        private PurchaseRequest insertPurchaseRequest(String prId, PurchaseRequestStatus status,
                int totalAmountExcludingTax) {
            PurchaseRequest request = new PurchaseRequest();
            request.setPrId(prId);
            request.setRequesterUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
            request.setDueDate(LocalDate.of(2026, 7, 7));
            request.setTotalAmountExcludingTax(totalAmountExcludingTax);
            request.setStatus(status);
            request.setCurrentStepOrder(1);
            purchaseRequestMapper.insertSelective(request);
            return purchaseRequestMapper.selectByPrimaryKey(prId);
        }

        private int insertPurchaseRequestDetail(String prDetailId, String prId, int lineNo, ItemKind kind,
                String supplierId, String supplierName, String itemName, String unitName, int unitPrice, int quantity) {
            int subtotal = unitPrice * quantity;
            PurchaseRequestDetail detail = new PurchaseRequestDetail();
            detail.setPrDetailId(prDetailId);
            detail.setPrId(prId);
            detail.setLineNo(lineNo);
            detail.setSnapKind(kind);
            detail.setSupplierId(supplierId);
            detail.setSnapSupplierName(supplierName);
            detail.setSnapItemName(itemName);
            detail.setSnapUnitName(unitName);
            detail.setSnapUnitPrice(unitPrice);
            detail.setQuantity(quantity);
            detail.setSubtotalExcludingTax(subtotal);
            purchaseRequestDetailMapper.insertSelective(detail);
            return subtotal;
        }

        class GoodsSelectionFixture {

            String supplierId = "c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a";

            PurchaseRequest threeDetailPr;

            PurchaseRequest oneDetailPr1;

            PurchaseRequest oneDetailPr2;

            String firstDetailId = "10000000-0000-4000-9000-000000000001";

            String secondDetailId = "10000000-0000-4000-9000-000000000102";

            String thirdDetailId = "10000000-0000-4000-9000-000000000103";

            String firstItemName = "Goods approved three detail 1";

            String firstUnitName = "piece";

            int firstUnitPrice = 1110;

            int firstQuantity = 2;

            int firstSubtotalExcludingTax = firstUnitPrice * firstQuantity;

            int threeDetailPrTotalAmountExcludingTax;

            int totalAmountExcludingTax;

        }

        class ServiceSelectionFixture {

            String supplierId = "c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a";

            String freeInputSupplierName = "Manual Service Supplier";

        }

    }

    @Nested
    class SelectPurchaseOrderCreateSource {

        String purchaserUserId = "6fe99043-cbd1-49c0-96d4-c156c58a8e60";

        String supplierId = "a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c";

        String firstDetailId = "33aaccfe-c7c4-4e37-ab48-259dbe7a7fdf";

        @Test
        void selectPurchaseOrderCreateSource_supplierIdSpecified_multiplePrDetails() {
            Supplier supplier = supplierMapper.selectByPrimaryKey(supplierId);
            Users purchaser = usersMapper.selectByPrimaryKey(purchaserUserId);
            PurchaseRequestDetail expectedFirstDetail = purchaseRequestDetailMapper.selectByPrimaryKey(firstDetailId);
            List<String> prDetailIds = List.of("72a9f12f-bb87-4604-93f6-5d866542b0d8", firstDetailId,
                    "fae1963c-2d5b-450a-8b12-4be480e65ecd");

            PurchaseOrderCreateSourceDto actual = purchaseOrderMapperCustom.selectPurchaseOrderCreateSource(supplierId,
                    supplier.getName(), purchaserUserId, prDetailIds);

            assertThat(actual.getMappingKey()).isEqualTo(supplierId);
            assertThat(actual.getSupplierName()).isEqualTo(supplier.getName());
            assertThat(actual.getPaymentTermName()).isEqualTo("30日後払い");
            assertThat(actual.getPurchaser())
                .isEqualTo(purchaser.getLastName() + " " + purchaser.getFirstName());
            assertThat(actual.getRelatedPrNumbers()).containsExactly(1, 3);
            assertThat(actual.getDetails()).hasSize(3);
            assertDetail(actual.getDetails().get(0), expectedFirstDetail);
        }

        @Test
        void selectPurchaseOrderCreateSource_supplierIdNull_singlePrDetail() {
            String supplierName = "Free Input Supplier";
            PurchaseRequestDetail expectedDetail = insertFreeInputDetail(supplierName);

            PurchaseOrderCreateSourceDto actual = purchaseOrderMapperCustom.selectPurchaseOrderCreateSource(null,
                    supplierName, purchaserUserId, List.of(expectedDetail.getPrDetailId()));

            assertThat(actual.getPaymentTermName()).isNull();
            assertThat(actual.getRelatedPrNumbers()).hasSize(1);
            assertThat(actual.getDetails()).hasSize(1);
            assertDetail(actual.getDetails().get(0), expectedDetail);
        }

        private PurchaseRequestDetail insertFreeInputDetail(String supplierName) {
            PurchaseRequestDetail detail = new PurchaseRequestDetail();
            detail.setPrDetailId("50000000-0000-4000-9000-000000000001");
            detail.setPrId("56856dfe-8e7a-4524-9d05-9e161c6b8fc3");
            detail.setLineNo(3);
            detail.setItemId("f758e462-f526-4b23-a822-8821c5c62adf");
            detail.setSnapItemName("Free Input Monitor");
            detail.setSnapKind(ItemKind.GOODS);
            detail.setUnitId("22222222-2222-2222-2222-222222222222");
            detail.setSnapUnitName("unit");
            detail.setSnapSupplierName(supplierName);
            detail.setQuantity(7);
            detail.setSnapUnitPrice(3210);
            detail.setSubtotalExcludingTax(22470);
            purchaseRequestDetailMapper.insertSelective(detail);
            return purchaseRequestDetailMapper.selectByPrimaryKey(detail.getPrDetailId());
        }

        private void assertDetail(PurchaseOrderCreateSourceRowDto actual, PurchaseRequestDetail expected) {
            assertThat(actual.getPrDetailId()).isEqualTo(expected.getPrDetailId());
            assertThat(actual.getItemId()).isEqualTo(expected.getItemId());
            assertThat(actual.getUnitId()).isEqualTo(expected.getUnitId());
            assertThat(actual.getItemName()).isEqualTo(expected.getSnapItemName());
            assertThat(actual.getUnitName()).isEqualTo(expected.getSnapUnitName());
            assertThat(actual.getUnitPrice()).isEqualTo(expected.getSnapUnitPrice());
            assertThat(actual.getPurchaseRequestQuantity()).isEqualTo(expected.getQuantity());
        }

    }

}
