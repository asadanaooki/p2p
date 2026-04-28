package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.PurchaseRequestDetailLineDto;
import com.example.p2p.dto.app.PurchaseRequestListViewDto;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.form.app.PurchaseRequestSearchForm;

@SpringBootTest
@Transactional
class PurchaseRequestServiceTest {

    @Autowired
    PurchaseRequestService purchaseRequestService;

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
        PurchaseRequestDetailDto actual = purchaseRequestService.getPurchaseRequestDetail("3c4f62bf-855b-4c35-b19d-eb06acb16896");
        
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
        PurchaseRequestDetailLineDto first = actual.getDetails().stream()
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

}
