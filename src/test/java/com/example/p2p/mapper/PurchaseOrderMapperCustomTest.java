package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.example.p2p.dto.app.PurchaseOrderDetailDto;
import com.example.p2p.dto.app.PurchaseOrderListRowDto;
import com.example.p2p.dto.app.PurchaseOrderRelatedPrDto;
import com.example.p2p.entity.PurchaseOrder;
import com.example.p2p.entity.PurchaseRequestPurchaseOrder;
import com.example.p2p.entity.Users;
import com.example.p2p.enums.PurchaseOrderSortBy;
import com.example.p2p.enums.PurchaseOrderStatus;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.app.PurchaseOrderSearchForm;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PurchaseOrderMapperCustomTest {

    @Autowired
    PurchaseOrderMapperCustom purchaseOrderMapperCustom;

    @Autowired
    PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    PurchaseRequestPurchaseOrderMapper purchaseRequestPurchaseOrderMapper;

    @Autowired
    UsersMapper usersMapper;

    @Nested
    class SelectPurchaseOrders {

        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";

        String firstPoId = "6f3b9242-2d1e-4e7a-b875-021c3f9a1a01";

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
            assertThat(first.getDueDate()).isEqualTo(LocalDate.of(2026, 6, 25));
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
                .extracting(PurchaseOrderRelatedPrDto::getPrId, PurchaseOrderRelatedPrDto::getDisplayNumber)
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
                .extracting(PurchaseOrderRelatedPrDto::getPrId, PurchaseOrderRelatedPrDto::getDisplayNumber)
                .containsExactly(tuple("3a5b130e-0279-4bca-bee3-d41cddbc9192", 2));
        }

        @Test
        void selectPurchaseOrderDetailHeader_self() {
            PurchaseOrderDetailDto actual = purchaseOrderMapperCustom.selectPurchaseOrderDetailHeader(standardPoId,
                    VisibilityScope.SELF, standardPurchaserUserId);

            assertThat(actual.getUserId()).isEqualTo(standardPurchaserUserId);
        }

    }

}
