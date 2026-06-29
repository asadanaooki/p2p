package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.app.PurchaseOrderDetailDto;
import com.example.p2p.dto.app.PurchaseOrderListRowDto;
import com.example.p2p.entity.PurchaseOrder;
import com.example.p2p.entity.PurchaseRequestPurchaseOrder;
import com.example.p2p.entity.Users;
import com.example.p2p.enums.PurchaseOrderSortBy;
import com.example.p2p.enums.PurchaseOrderStatus;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.enums.PurchaseRequestStatus;
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

        @Test
        void selectPurchaseOrders_noCondition() {
            List<PurchaseOrderListRowDto> actual = purchaseOrderMapperCustom
                .selectPurchaseOrders(new PurchaseOrderSearchForm(), VisibilityScope.ALL, userId);

            assertThat(actual).hasSize(2);

            PurchaseOrderListRowDto first = actual.get(0);
            assertThat(first.getPoId()).isEqualTo("6f3b9242-2d1e-4e7a-b875-021c3f9a1a01");
            assertThat(first.getDisplayNumber()).isOne();
            assertThat(first.getOrderType()).isEqualTo(PurchaseOrderType.STANDARD);
            assertThat(first.getPurchaser()).isEqualTo("山田 太郎");
            assertThat(first.getDueDate()).isEqualTo(LocalDate.of(2026, 6, 25));
            assertThat(first.getSupplierName()).isEqualTo("中部設備サプライ株式会社");
            assertThat(first.getTotalAmountExcludingTax()).isEqualTo(3200);
            assertThat(first.getStatus()).isEqualTo(PurchaseOrderStatus.PENDING);
        }

        @ParameterizedTest
        @CsvSource(value = { "設備, 1", "カナガワ, 2" })
        void selectPurchaseOrders_bySupplierKeyword(String keyword, int displayNumber) {
            PurchaseOrderSearchForm form = new PurchaseOrderSearchForm();
            form.setKeyword(keyword);

            List<PurchaseOrderListRowDto> actual = purchaseOrderMapperCustom.selectPurchaseOrders(form,
                    VisibilityScope.ALL, userId);
            assertThat(actual).hasSize(2);
            assertThat(actual.get(0).getDisplayNumber()).isEqualTo(displayNumber);
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
                    Arguments.of(PurchaseOrderSortBy.TYPE, SortDirection.DESC, 6, 3),
                    Arguments.of(PurchaseOrderSortBy.PURCHASER, SortDirection.ASC, 3, 5),
                    Arguments.of(PurchaseOrderSortBy.SUPPLIER, SortDirection.DESC, 4, 1));
        }

    }

    @Nested
    class SelectPurchaseOrderDetailHeader {

        String standardPoId = "6f3b9242-2d1e-4e7a-b875-021c3f9a1a01";
        String servicePoId = "f7e34df9-f4cb-4c88-82c2-1fdfc7dd8a03";
        String standardPurchaserUserId = "36a1d5d9-15b8-45d5-8ae7-607244bbe36e";
        String servicePurchaserUserId = "169f1e17-619f-45bf-b6dc-8faed08c404c";
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

            Users purchaser = usersMapper.selectByPrimaryKey(standardPurchaserUserId);
            String expectedPurchaser = purchaser.getLastName() + " " + purchaser.getFirstName();

            PurchaseOrderDetailDto actual = purchaseOrderMapperCustom.selectPurchaseOrderDetailHeader(standardPoId,
                    VisibilityScope.ALL, servicePurchaserUserId);

            assertThat(actual.getPoId()).isEqualTo(standardPoId);
            assertThat(actual.getDisplayNumber()).isOne();
            assertThat(actual.getOrderType()).isEqualTo(PurchaseOrderType.STANDARD);
            assertThat(actual.getSupplierName()).isEqualTo("中部設備サプライ株式会社");
            assertThat(actual.getUserId()).isEqualTo(standardPurchaserUserId);
            assertThat(actual.getPurchaser()).isEqualTo(expectedPurchaser);
            assertThat(actual.getTotalAmountExcludingTax()).isEqualTo(3200);
            assertThat(actual.getStatus()).isEqualTo(PurchaseRequestStatus.PENDING);
            assertThat(actual.getNote()).isNull();
            assertThat(actual.getCreatedAt()).isEqualTo(LocalDate.of(2026, 6, 15));
            assertThat(actual.getDeliveryDueDate()).isEqualTo(LocalDate.of(2026, 6, 25));
            assertThat(actual.getServicePeriodFrom()).isNull();
            assertThat(actual.getServicePeriodTo()).isNull();
            assertThat(actual.getRelatedPrDisplayNumbers()).containsExactly(1, 2);
        }

        @Test
        void selectPurchaseOrderDetailHeader_all_serviceOrder() {
            LocalDateTime createdAt = LocalDateTime.of(2026, 6, 16, 9, 15);
            PurchaseOrder order = new PurchaseOrder();
            order.setPoId(servicePoId);
            order.setCreatedAt(createdAt);
            purchaseOrderMapper.updateByPrimaryKeySelective(order);

            Users purchaser = usersMapper.selectByPrimaryKey(servicePurchaserUserId);
            String expectedPurchaser = purchaser.getLastName() + " " + purchaser.getFirstName();

            PurchaseOrderDetailDto actual = purchaseOrderMapperCustom.selectPurchaseOrderDetailHeader(servicePoId,
                    VisibilityScope.ALL, standardPurchaserUserId);

            assertThat(actual.getPoId()).isEqualTo(servicePoId);
            assertThat(actual.getDisplayNumber()).isEqualTo(3);
            assertThat(actual.getOrderType()).isEqualTo(PurchaseOrderType.SERVICE);
            assertThat(actual.getSupplierName()).isEqualTo("関西オフィスサービス株式会社");
            assertThat(actual.getUserId()).isEqualTo(servicePurchaserUserId);
            assertThat(actual.getPurchaser()).isEqualTo(expectedPurchaser);
            assertThat(actual.getTotalAmountExcludingTax()).isEqualTo(365000);
            assertThat(actual.getStatus()).isEqualTo(PurchaseRequestStatus.APPROVED);
            assertThat(actual.getNote()).isNull();
            assertThat(actual.getCreatedAt()).isEqualTo(LocalDate.of(2026, 6, 16));
            assertThat(actual.getDeliveryDueDate()).isNull();
            assertThat(actual.getServicePeriodFrom()).isEqualTo(LocalDate.of(2026, 2, 1));
            assertThat(actual.getServicePeriodTo()).isEqualTo(LocalDate.of(2026, 7, 10));
            assertThat(actual.getRelatedPrDisplayNumbers()).containsExactly(3);
        }

        @Test
        void selectPurchaseOrderDetailHeader_self() {
            PurchaseOrderDetailDto actual = purchaseOrderMapperCustom.selectPurchaseOrderDetailHeader(standardPoId,
                    VisibilityScope.SELF, standardPurchaserUserId);

            assertThat(actual.getUserId()).isEqualTo(standardPurchaserUserId);
        }
    }

}
