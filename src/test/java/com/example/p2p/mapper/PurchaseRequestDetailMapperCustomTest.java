package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.app.PurchaseRequestDetailLineDto;
import com.example.p2p.entity.PurchaseRequestDetail;
import com.example.p2p.entity.PurchaseRequestDetailExample;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.VisibilityScope;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PurchaseRequestDetailMapperCustomTest {

    @Autowired
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    @Autowired
    PurchaseRequestDetailMapperCustom purchaseRequestDetailMapperCustom;

    @Test
    void selectPurchaseRequestDetailLines_orderByLineNo() {
        updateLineNo("fae1963c-2d5b-450a-8b12-4be480e65ecd", 2);
        updateLineNo("72a9f12f-bb87-4604-93f6-5d866542b0d8", 3);
        updateLineNo("ad3e8b17-092a-4c73-acc4-1b799a5f5e97", 1);

        List<PurchaseRequestDetailLineDto> actual = purchaseRequestDetailMapperCustom.selectPurchaseRequestDetailLines(
                "3c4f62bf-855b-4c35-b19d-eb06acb16896", VisibilityScope.ALL,
                "36a1d5d9-15b8-45d5-8ae7-607244bbe36e");

        assertThat(actual).extracting(PurchaseRequestDetailLineDto::getUnitPrice,
                PurchaseRequestDetailLineDto::getQuantity, PurchaseRequestDetailLineDto::getSubtotalExcludingTax)
            .containsExactly(tuple(16800, 2, 33600), tuple(680, 5, 3400), tuple(980, 3, 2940));
    }

    @Nested
    class BulkUpsert {

        @Test
        void bulkUpsert() {
            PurchaseRequestDetail free = new PurchaseRequestDetail();

            free.setPrDetailId("fae1963c-2d5b-450a-8b12-4be480e65ecd");
            free.setPrId("3c4f62bf-855b-4c35-b19d-eb06acb16896");
            free.setItemId(null);
            free.setSnapItemName("A4コピー用紙 500枚");
            free.setSnapKind(ItemKind.GOODS);
            free.setUnitId("22222222-2222-2222-2222-222222222221");
            free.setSnapUnitName("個");
            free.setSupplierId("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            free.setSnapSupplierName("神奈川文具株式会社");
            free.setQuantity(5);
            free.setSnapUnitPrice(680);
            free.setSubtotalExcludingTax(3400);
            free.setCreatedAt(LocalDateTime.of(2026, 4, 26, 14, 40, 21, 434_000_000));
            free.setUpdatedAt(LocalDateTime.of(2026, 5, 2, 19, 56, 0, 433_000_000));
            free.setLineNo(3);
            purchaseRequestDetailMapper.updateByPrimaryKey(free);

            // 既存カタログ
            PurchaseRequestDetail prd1 = new PurchaseRequestDetail();
            prd1.setPrDetailId("72a9f12f-bb87-4604-93f6-5d866542b0d8");
            prd1.setPrId("3c4f62bf-855b-4c35-b19d-eb06acb16896");
            prd1.setItemId("1bd0d872-69b1-4999-b522-ac202c481662");
            prd1.setLineNo(2);
            prd1.setQuantity(3);
            prd1.setSnapUnitPrice(980);
            prd1.setSubtotalExcludingTax(2940);

            // 既存カタログ
            PurchaseRequestDetail prd2 = new PurchaseRequestDetail();
            prd2.setPrDetailId("ad3e8b17-092a-4c73-acc4-1b799a5f5e97");
            prd2.setPrId("3c4f62bf-855b-4c35-b19d-eb06acb16896");
            prd2.setItemId("f758e462-f526-4b23-a822-8821c5c62adf");
            prd2.setLineNo(1);
            prd2.setQuantity(10);
            prd2.setSnapUnitPrice(100);
            prd2.setSubtotalExcludingTax(168000);

            // 既存フリー入力
            PurchaseRequestDetail prd3 = new PurchaseRequestDetail();
            prd3.setPrDetailId("fae1963c-2d5b-450a-8b12-4be480e65ecd");
            prd3.setPrId("3c4f62bf-855b-4c35-b19d-eb06acb16896");
            prd3.setItemId(null);
            prd3.setLineNo(3);
            prd3.setQuantity(1);
            prd3.setSnapUnitPrice(5000);
            prd3.setSubtotalExcludingTax(5000);
            prd3.setSnapItemName("清掃サービス");
            prd3.setSnapKind(ItemKind.SERVICE);
            prd3.setUnitId(null);
            prd3.setSnapUnitName("組");
            prd3.setSupplierId(null);
            prd3.setSnapSupplierName("testSup");

            // 新規ふりー入力
            PurchaseRequestDetail prd4 = new PurchaseRequestDetail();
            prd4.setPrId("3c4f62bf-855b-4c35-b19d-eb06acb16896");
            prd4.setItemId(null);
            prd4.setLineNo(4);
            prd4.setQuantity(1);
            prd4.setSnapUnitPrice(2000);
            prd4.setSubtotalExcludingTax(2000);
            prd4.setSnapItemName("PC研修");
            prd4.setSnapKind(ItemKind.SERVICE);
            prd4.setUnitId(null);
            prd4.setSnapUnitName("セクション");
            prd4.setSupplierId(null);
            prd4.setSnapSupplierName("testSup12");

            purchaseRequestDetailMapperCustom.bulkUpsert(List.of(prd1, prd2, prd3, prd4));

            PurchaseRequestDetailExample ex = new PurchaseRequestDetailExample();
            ex.createCriteria().andPrIdEqualTo("3c4f62bf-855b-4c35-b19d-eb06acb16896");
            ex.setOrderByClause("line_no asc");
            List<PurchaseRequestDetail> actual = purchaseRequestDetailMapper.selectByExample(ex);
            assertThat(actual).hasSize(4);

            PurchaseRequestDetail first = actual.get(0);
            assertThat(first.getLineNo()).isEqualTo(1);
            assertThat(first.getQuantity()).isEqualTo(10);
            assertThat(first.getSnapUnitPrice()).isEqualTo(16800);
            assertThat(first.getSubtotalExcludingTax()).isEqualTo(168000);
            assertThat(first.getItemId()).isEqualTo("f758e462-f526-4b23-a822-8821c5c62adf");
            assertThat(first.getSnapItemName()).isEqualTo("24インチ液晶モニター");
            assertThat(first.getSnapKind()).isEqualTo(ItemKind.GOODS);
            assertThat(first.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222222");
            assertThat(first.getSnapUnitName()).isEqualTo("台");
            assertThat(first.getSupplierId()).isEqualTo("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
            assertThat(first.getSnapSupplierName()).isEqualTo("関西オフィスサービス株式会社");
            assertThat(first.getCreatedAt()).isNotNull();
            assertThat(first.getUpdatedAt()).isNotNull();

            PurchaseRequestDetail second = actual.get(1);
            assertThat(second.getLineNo()).isEqualTo(2);
            assertThat(second.getQuantity()).isEqualTo(3);
            assertThat(second.getSnapUnitPrice()).isEqualTo(980);
            assertThat(second.getSubtotalExcludingTax()).isEqualTo(2940);
            assertThat(second.getCreatedAt()).isNotNull();
            assertThat(second.getUpdatedAt()).isNotNull();

            PurchaseRequestDetail third = actual.get(2);
            assertThat(third.getLineNo()).isEqualTo(3);
            assertThat(third.getQuantity()).isEqualTo(1);
            assertThat(third.getSnapUnitPrice()).isEqualTo(5000);
            assertThat(third.getSubtotalExcludingTax()).isEqualTo(5000);
            assertThat(third.getItemId()).isNull();
            assertThat(third.getSnapItemName()).isEqualTo("清掃サービス");
            assertThat(third.getSnapKind()).isEqualTo(ItemKind.SERVICE);
            assertThat(third.getUnitId()).isNull();
            ;
            assertThat(third.getSnapUnitName()).isEqualTo("組");
            assertThat(third.getSupplierId()).isNull();
            assertThat(third.getSnapSupplierName()).isEqualTo("testSup");
            assertThat(third.getCreatedAt()).isNotNull();
            assertThat(third.getUpdatedAt()).isNotNull();

            PurchaseRequestDetail fourth = actual.get(3);
            assertThat(fourth.getPrDetailId().length()).isEqualTo(36);
            assertThat(fourth.getLineNo()).isEqualTo(4);
            assertThat(fourth.getQuantity()).isEqualTo(1);
            assertThat(fourth.getSnapUnitPrice()).isEqualTo(2000);
            assertThat(fourth.getSubtotalExcludingTax()).isEqualTo(2000);
            assertThat(fourth.getItemId()).isNull();
            assertThat(fourth.getSnapItemName()).isEqualTo("PC研修");
            assertThat(fourth.getSnapKind()).isEqualTo(ItemKind.SERVICE);
            assertThat(fourth.getUnitId()).isNull();
            ;
            assertThat(fourth.getSnapUnitName()).isEqualTo("セクション");
            assertThat(fourth.getSupplierId()).isNull();
            assertThat(fourth.getSnapSupplierName()).isEqualTo("testSup12");
            assertThat(fourth.getCreatedAt()).isNotNull();
            assertThat(fourth.getUpdatedAt()).isNotNull();

        }
        
        @Test
        void bulkUpsert_newCatalog() {
            PurchaseRequestDetail prd = new PurchaseRequestDetail();
            prd.setPrId("88bfbcf6-2be6-4d31-8a46-155a7b58ab93");
            prd.setItemId("1bd0d872-69b1-4999-b522-ac202c481662");
            prd.setLineNo(2);
            prd.setQuantity(1);
            prd.setSnapUnitPrice(980);
            prd.setSubtotalExcludingTax(980);
            prd.setSnapItemName("油性ボールペン 黒 10本セット");
            prd.setSnapKind(ItemKind.GOODS);
            prd.setUnitId("22222222-2222-2222-2222-222222222221");
            prd.setSnapUnitName("個");
            prd.setSupplierId("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            prd.setSnapSupplierName("神奈川文具株式会社");
            
            purchaseRequestDetailMapperCustom.bulkUpsert(List.of(prd));
            
            PurchaseRequestDetailExample ex = new PurchaseRequestDetailExample();
            ex.createCriteria().andPrIdEqualTo("88bfbcf6-2be6-4d31-8a46-155a7b58ab93");
            ex.setOrderByClause("line_no asc");
            List<PurchaseRequestDetail> actual = purchaseRequestDetailMapper.selectByExample(ex);
            assertThat(actual).hasSize(2);

            PurchaseRequestDetail first = actual.get(1);
            assertThat(first.getLineNo()).isEqualTo(2);
            assertThat(first.getQuantity()).isEqualTo(1);
            assertThat(first.getSnapUnitPrice()).isEqualTo(980);
            assertThat(first.getSubtotalExcludingTax()).isEqualTo(980);
            assertThat(first.getItemId()).isEqualTo("1bd0d872-69b1-4999-b522-ac202c481662");
            assertThat(first.getSnapItemName()).isEqualTo("油性ボールペン 黒 10本セット");
            assertThat(first.getSnapKind()).isEqualTo(ItemKind.GOODS);
            assertThat(first.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222221");
            assertThat(first.getSnapUnitName()).isEqualTo("個");
            assertThat(first.getSupplierId()).isEqualTo("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            assertThat(first.getSnapSupplierName()).isEqualTo("神奈川文具株式会社");
            assertThat(first.getCreatedAt()).isNotNull();
            assertThat(first.getUpdatedAt()).isNotNull();
        }

    }

    private void updateLineNo(String prDetailId, int lineNo) {
        PurchaseRequestDetail detail = new PurchaseRequestDetail();
        detail.setPrDetailId(prDetailId);
        detail.setLineNo(lineNo);
        purchaseRequestDetailMapper.updateByPrimaryKeySelective(detail);
    }

}
