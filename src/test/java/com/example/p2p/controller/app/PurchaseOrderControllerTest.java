package com.example.p2p.controller.app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.p2p.dto.app.PurchaseOrderDetailSelectionViewDto;
import com.example.p2p.dto.app.PurchaseOrderListViewDto;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.service.app.PurchaseOrderService;

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseOrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PurchaseOrderService purchaseOrderService;

    @Test
    @WithMockUser(authorities = { "PO_VIEW_SELF" })
    void showPurchaseOrderList_storesOwnSearchCondition() throws Exception {
        doReturn(new PurchaseOrderListViewDto()).when(purchaseOrderService).searchPurchaseOrders(any(), any());

        mockMvc.perform(get("/purchase-order"))
            .andExpect(status().isOk())
            .andExpect(request().sessionAttribute("purchaseOrderLastSearchCondition", notNullValue()));
    }

    @Nested
    class ShowDetailSelectionFromPrs {

        @WithMockUser(authorities = { "PO_VIEW_SELF" })
        @Test
        void showDetailSelectionFromPrs_withoutPoCreateAuthority() throws Exception {
            doReturn(new PurchaseOrderDetailSelectionViewDto()).when(purchaseOrderService)
                .getDetailSelectionView(any(), any(), any());
            mockMvc
                .perform(get("/purchase-order/create/detail-selection")
                        .param("orderType", PurchaseOrderType.STANDARD.toString())
                        .param("supplierId", "aaa")
                        .param("supplierName", "")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        }
        
        @WithMockUser(authorities = {"PO_CREATE" ,"PO_VIEW_SELF" })
        @Test
        void showDetailSelectionFromPrs_PoCreateSelfOnly() throws Exception {
            doReturn(new PurchaseOrderDetailSelectionViewDto()).when(purchaseOrderService)
                .getDetailSelectionView(any(), any(), any());
            mockMvc
                .perform(get("/purchase-order/create/detail-selection")
                        .param("orderType", PurchaseOrderType.STANDARD.toString())
                        .param("supplierId", "aaa")
                        .param("supplierName", "")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        }
        
        @WithMockUser(authorities = {"PO_CREATE" ,"PO_VIEW_ALL" })
        @Test
        void showDetailSelectionFromPrs_PoCreate() throws Exception {
            doReturn(new PurchaseOrderDetailSelectionViewDto()).when(purchaseOrderService)
                .getDetailSelectionView(any(), any(), any());
            mockMvc
                .perform(get("/purchase-order/create/detail-selection")
                        .param("orderType", PurchaseOrderType.STANDARD.toString())
                        .param("supplierId", "aaa")
                        .param("supplierName", "")
                        .with(csrf()))
                .andExpect(status().isOk());

        }

    }

}
