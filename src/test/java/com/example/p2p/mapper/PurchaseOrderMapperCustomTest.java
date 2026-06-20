package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.example.p2p.dto.app.PurchaseOrderListRowDto;
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
            assertThat(first.getDueDate()).isNull();
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

}
