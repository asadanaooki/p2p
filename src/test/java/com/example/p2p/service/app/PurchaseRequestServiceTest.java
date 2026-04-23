package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.PurchaseRequestListViewDto;
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
        assertThat(actual.getSupplierOptions()).hasSize(3);
        assertThat(actual.getPurchaseRequests()).hasSize(2);

        assertThat(actual.getPurchaseRequests().get(0).getPrId()).isEqualTo("88bfbcf6-2be6-4d31-8a46-155a7b58ab93");

    }

}
