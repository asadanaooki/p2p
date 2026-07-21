package com.example.p2p.controller.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.p2p.dto.app.AuthenticationUserDto;
import com.example.p2p.dto.app.PurchaseOrderCreateViewDto;
import com.example.p2p.dto.app.PurchaseOrderDetailSelectionPrGroupDto;
import com.example.p2p.dto.app.PurchaseOrderDetailSelectionRowDto;
import com.example.p2p.dto.app.PurchaseOrderDetailSelectionViewDto;
import com.example.p2p.dto.app.PurchaseOrderListViewDto;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.app.PurchaseOrderCreatePreparationForm;
import com.example.p2p.security.CustomUserDetails;
import com.example.p2p.service.app.PurchaseOrderService;
import com.example.p2p.session.app.PurchaseOrderCreateDraft;

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
    class ShowDetailSelectionFromPr {

        @WithMockUser(authorities = { "PO_VIEW_SELF" })
        @Test
        void withoutPoCreateAuthority_forbidden() throws Exception {
            mockMvc.perform(get("/purchase-order/create/detail-selection")
                    .param("orderType", PurchaseOrderType.STANDARD.toString()))
                .andExpect(status().isForbidden());

            verify(purchaseOrderService, never()).getDetailSelectionView(any(), any(), any());
        }

        @WithMockUser(authorities = { "PO_CREATE" })
        @Test
        void populatesFormFromTwoPrGroupsInViewOrder() throws Exception {
            String supplierId = "supplier-1";
            String supplierName = "テスト仕入先";
            PurchaseOrderDetailSelectionViewDto expectedView = detailSelectionView();
            doReturn(expectedView).when(purchaseOrderService)
                .getDetailSelectionView(PurchaseOrderType.STANDARD, supplierId, supplierName);

            MvcResult result = mockMvc.perform(get("/purchase-order/create/detail-selection")
                    .param("orderType", PurchaseOrderType.STANDARD.toString())
                    .param("supplierId", supplierId)
                    .param("supplierName", supplierName))
                .andExpect(status().isOk())
                .andExpect(view().name("app/purchase-order-detail-selection"))
                .andExpect(model().attribute("view", expectedView))
                .andReturn();

            PurchaseOrderCreatePreparationForm form = modelAttribute(result, "form",
                    PurchaseOrderCreatePreparationForm.class);
            assertThat(form.getSupplierId()).isEqualTo(supplierId);
            assertThat(form.getSupplierName()).isEqualTo(supplierName);
            assertThat(form.getOrderType()).isEqualTo(PurchaseOrderType.STANDARD);
            assertThat(form.getDetails()).hasSize(4);
            assertThat(form.getDetails())
                .extracting(PurchaseOrderCreatePreparationForm.PurchaseRequestDetailSelectionRowForm::getPrDetailId)
                .containsExactly("detail-1", "detail-2", "detail-3", "detail-4");

            PurchaseOrderCreatePreparationForm.PurchaseRequestDetailSelectionRowForm first = form.getDetails().get(0);
            assertThat(first.getPrDetailId()).isEqualTo("detail-1");
            assertThat(first.getSelectedQuantity()).isEqualTo(10);
            assertThat(first.getSelected()).isFalse();

            verify(purchaseOrderService).getDetailSelectionView(PurchaseOrderType.STANDARD, supplierId,
                    supplierName);
        }
    }

    @Nested
    class CreatePurchaseOrderDraft {

        @WithMockUser(authorities = { "PO_CREATE" })
        @Test
        void detailValidationError_restoresView() throws Exception {
            String supplierId = "supplier-1";
            String supplierName = "テスト仕入先";
            PurchaseOrderDetailSelectionViewDto expectedView = detailSelectionView();
            doReturn(expectedView).when(purchaseOrderService)
                .getDetailSelectionView(PurchaseOrderType.STANDARD, supplierId, supplierName);

            MvcResult result = mockMvc.perform(post("/purchase-order/create/draft")
                    .param("supplierId", supplierId)
                    .param("supplierName", supplierName)
                    .param("orderType", PurchaseOrderType.STANDARD.toString())
                    .param("details[0].prDetailId", "detail-1")
                    .param("details[0].selectedQuantity", "0")
                    .param("details[0].selected", "true")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("app/purchase-order-detail-selection"))
                .andExpect(model().attribute("view", expectedView))
                .andReturn();

            BindingResult bindingResult = (BindingResult) result.getModelAndView()
                .getModel()
                .get(BindingResult.MODEL_KEY_PREFIX + "form");
            assertThat(bindingResult.hasFieldErrors("details[0].selectedQuantity")).isTrue();
            assertThat(bindingResult.hasFieldErrors("orderType")).isFalse();
            assertThat(bindingResult.hasFieldErrors("supplierName")).isFalse();

            verify(purchaseOrderService).getDetailSelectionView(PurchaseOrderType.STANDARD, supplierId,
                    supplierName);
        }

        @WithMockUser(authorities = { "PO_CREATE" })
        @Test
        void validForm_savesOnlySelectedDetailsAndRedirectsWithDraftId() throws Exception {
            String supplierId = "supplier-1";
            String supplierName = "テスト仕入先";

            MvcResult result = mockMvc.perform(post("/purchase-order/create/draft")
                    .param("supplierId", supplierId)
                    .param("supplierName", supplierName)
                    .param("orderType", PurchaseOrderType.STANDARD.toString())
                    .param("details[0].prDetailId", "detail-1")
                    .param("details[0].selectedQuantity", "2")
                    .param("details[0].selected", "true")
                    .param("details[1].prDetailId", "detail-2")
                    .param("details[1].selectedQuantity", "3")
                    .param("details[1].selected", "false")
                    .param("details[2].prDetailId", "detail-3")
                    .param("details[2].selectedQuantity", "4")
                    .param("details[2].selected", "true")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

            String redirectedUrl = result.getResponse().getRedirectedUrl();
            assertThat(redirectedUrl).startsWith("/purchase-order/create?draftId=");
            String draftId = UriComponentsBuilder.fromUriString(redirectedUrl)
                .build()
                .getQueryParams()
                .getFirst("draftId");
            assertThat(draftId).isNotBlank();

            PurchaseOrderCreateDraft draft = (PurchaseOrderCreateDraft) result.getRequest()
                .getSession(false)
                .getAttribute(draftId);
            assertThat(draft).isNotNull();
            assertThat(draft.getSupplierId()).isEqualTo(supplierId);
            assertThat(draft.getSupplierName()).isEqualTo(supplierName);
            assertThat(draft.getOrderType()).isEqualTo(PurchaseOrderType.STANDARD);
            assertThat(draft.getDetails()).hasSize(2);
            assertThat(draft.getDetails())
                .extracting(PurchaseOrderCreateDraft.SelectedPurchaseRequestDetail::getPrDetailId)
                .containsExactly("detail-1", "detail-3");
            assertThat(draft.getDetails().get(0).getPrDetailId()).isEqualTo("detail-1");
            assertThat(draft.getDetails().get(0).getSelectedQuantity()).isEqualTo(2);
        }
    }

    @Nested
    class ShowPurchaseOrderCreateForm {

        @Test
        void directCreation_setsPurchaserAndLeavesOtherViewFieldsNull() throws Exception {
            CustomUserDetails loginUser = loginUser("user-1", "テスト 太郎");

            MvcResult result = mockMvc.perform(get("/purchase-order/create").with(user(loginUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("app/purchase-order-create"))
                .andExpect(model().attributeExists("view"))
                .andReturn();

            PurchaseOrderCreateViewDto direct = modelAttribute(result, "view", PurchaseOrderCreateViewDto.class);
            assertThat(direct.getPurchaser()).isEqualTo("テスト 太郎");
            assertThat(direct.getSupplierId()).isNull();
            assertThat(direct.getSupplierName()).isNull();
            assertThat(direct.getRelatedPrNumbers()).isNull();
            assertThat(direct.getLines()).isNull();

            verify(purchaseOrderService, never()).getPurchaseOrderCreateView(any(), any());
        }

        @Test
        void expiredDraft_redirectsWithFlashErrorMessage() throws Exception {
            CustomUserDetails loginUser = loginUser("user-1", "テスト 太郎");

            mockMvc.perform(get("/purchase-order/create")
                    .param("draftId", "expired-draft")
                    .with(user(loginUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchase-order/create"))
                .andExpect(flash().attribute("errorMessage", "発注書の作成情報が期限切れになりました。"));

            verify(purchaseOrderService, never()).getPurchaseOrderCreateView(any(), any());
        }

        @Test
        void creationFromPr_addsServiceViewToModel() throws Exception {
            String userId = "user-1";
            String draftId = "draft-1";
            CustomUserDetails loginUser = loginUser(userId, "テスト 太郎");
            PurchaseOrderCreateDraft draft = new PurchaseOrderCreateDraft("supplier-1", "テスト仕入先",
                    PurchaseOrderType.STANDARD,
                    List.of(new PurchaseOrderCreateDraft.SelectedPurchaseRequestDetail("detail-1", 2)));
            PurchaseOrderCreateViewDto expectedView = new PurchaseOrderCreateViewDto();
            expectedView.setSupplierId("supplier-1");
            doReturn(expectedView).when(purchaseOrderService).getPurchaseOrderCreateView(same(draft), eq(userId));

            mockMvc.perform(get("/purchase-order/create")
                    .param("draftId", draftId)
                    .sessionAttr(draftId, draft)
                    .with(user(loginUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("app/purchase-order-create"))
                .andExpect(model().attribute("view", expectedView));

            verify(purchaseOrderService).getPurchaseOrderCreateView(same(draft), eq(userId));
        }
    }

    private PurchaseOrderDetailSelectionViewDto detailSelectionView() {
        PurchaseOrderDetailSelectionPrGroupDto firstGroup = new PurchaseOrderDetailSelectionPrGroupDto();
        firstGroup.setPrId("pr-1");
        firstGroup.setDetailRows(List.of(detailRow("detail-1", 0, 10), detailRow("detail-2", 1, 20)));

        PurchaseOrderDetailSelectionPrGroupDto secondGroup = new PurchaseOrderDetailSelectionPrGroupDto();
        secondGroup.setPrId("pr-2");
        secondGroup.setDetailRows(List.of(detailRow("detail-3", 2, 30), detailRow("detail-4", 3, 40)));

        PurchaseOrderDetailSelectionViewDto view = new PurchaseOrderDetailSelectionViewDto();
        view.setPrGroups(List.of(firstGroup, secondGroup));
        return view;
    }

    private PurchaseOrderDetailSelectionRowDto detailRow(String prDetailId, int detailIndex, int quantity) {
        PurchaseOrderDetailSelectionRowDto row = new PurchaseOrderDetailSelectionRowDto();
        row.setPrDetailId(prDetailId);
        row.setDetailIndex(detailIndex);
        row.setQuantity(quantity);
        return row;
    }

    private CustomUserDetails loginUser(String userId, String fullName) {
        AuthenticationUserDto authUser = new AuthenticationUserDto();
        authUser.setUserId(userId);
        authUser.setFullName(fullName);
        authUser.setPasswordHash("password");
        authUser.setEmail("test@example.com");
        authUser.setRoleName("ROLE_TEST");
        authUser.setPrCreate(false);
        authUser.setPoCreate(true);
        authUser.setReceiptCreate(false);
        authUser.setInvoiceCreate(false);
        authUser.setPrViewScope(VisibilityScope.NONE);
        authUser.setPoViewScope(VisibilityScope.NONE);
        authUser.setReceiptViewScope(VisibilityScope.NONE);
        authUser.setInvoiceViewScope(VisibilityScope.NONE);
        authUser.setPrApprove(false);
        authUser.setPoApprove(false);
        authUser.setInvoiceApprove(false);
        authUser.setSettingManage(false);
        return new CustomUserDetails(authUser);
    }

    private <T> T modelAttribute(MvcResult result, String name, Class<T> type) {
        return type.cast(result.getModelAndView().getModel().get(name));
    }

}
