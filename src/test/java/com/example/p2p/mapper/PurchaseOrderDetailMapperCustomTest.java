package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.app.PurchaseOrderDetailLineDto;
import com.example.p2p.entity.PurchaseOrder;
import com.example.p2p.enums.VisibilityScope;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PurchaseOrderDetailMapperCustomTest {

    @Autowired
    PurchaseOrderDetailMapperCustom purchaseOrderDetailMapperCustom;

    @Autowired
    PurchaseOrderMapper purchaseOrderMapper;

    @Nested
    class SelectPurchaseOrderDetailLines {

        String standardPoId = "6f3b9242-2d1e-4e7a-b875-021c3f9a1a01";
        String servicePoId = "3d062413-54a0-40f0-b764-3d1c6e4f5a02";
        String otherUserId = "6fe99043-cbd1-49c0-96d4-c156c58a8e60";

        @Test
        void selectPurchaseOrderDetailLines_all_standardPo() {
            List<PurchaseOrderDetailLineDto> actual = purchaseOrderDetailMapperCustom
                .selectPurchaseOrderDetailLines(standardPoId, VisibilityScope.ALL, otherUserId);

            assertThat(actual).hasSize(1);

            PurchaseOrderDetailLineDto line = actual.get(0);
            assertThat(line.getLineNo()).isOne();
            assertThat(line.getItemName()).isNotBlank();
            assertThat(line.getLineAmountExcludingTax()).isEqualTo(6800);
            assertThat(line.getUnitName()).isNotBlank();
            assertThat(line.getQuantity()).isEqualTo(10);
            assertThat(line.getUnitPrice()).isEqualTo(680);
        }

        @Test
        void selectPurchaseOrderDetailLines_all_serviceOrder() {
            List<PurchaseOrderDetailLineDto> actual = purchaseOrderDetailMapperCustom
                .selectPurchaseOrderDetailLines(servicePoId, VisibilityScope.ALL, otherUserId);

            assertThat(actual).hasSize(1);

            PurchaseOrderDetailLineDto line = actual.get(0);
            assertThat(line.getLineNo()).isOne();
            assertThat(line.getItemName()).isNotBlank();
            assertThat(line.getLineAmountExcludingTax()).isEqualTo(25000);
            assertThat(line.getUnitName()).isNull();
            assertThat(line.getQuantity()).isNull();
            assertThat(line.getUnitPrice()).isNull();
        }

        @Test
        void selectPurchaseOrderDetailLines_self() {
            PurchaseOrder order = purchaseOrderMapper.selectByPrimaryKey(standardPoId);
            assertThat(order).isNotNull();

            List<PurchaseOrderDetailLineDto> actual = purchaseOrderDetailMapperCustom
                .selectPurchaseOrderDetailLines(standardPoId, VisibilityScope.SELF, order.getPurchaserUserId());

            assertThat(actual).hasSize(1);
        }
    }
}
