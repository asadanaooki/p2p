package com.example.p2p.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import com.example.p2p.dto.ItemEditViewDto;
import com.example.p2p.dto.ItemListViewDto;
import com.example.p2p.dto.SupplierOptionDto;
import com.example.p2p.dto.UnitOptionDto;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.ItemSortBy;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.form.ItemEditForm;
import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.service.ItemService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ItemService itemService;
    
    @Autowired
    MockHttpServletRequest mockHttpServletRequest;
    
    @Nested
    class ShowItem{
        @ParameterizedTest
        @MethodSource("createOkCases")
        void showItem_parameter_ok(String name, String value) throws Exception {
            doReturn(new ItemListViewDto()).when(itemService).searchItems(any());
            mockMvc
            .perform(get("/setting/item").with(csrf())
                    .param(name, value))
            .andExpect(request().sessionAttribute("lastSearchCondition", notNullValue()))
            .andExpect(model().attributeExists("view"))
            .andExpect(model().attributeHasNoErrors("form"))
            .andExpect(view().name("item-list"));
        }
        
        @ParameterizedTest
        @MethodSource("createNgCases")
        void showItem_parameter_ng(String name, String value) throws Exception {
            doReturn(new ItemListViewDto()).when(itemService).searchItems(any());
            
            mockMvc
            .perform(get("/setting/item").with(csrf())
                    .param(name, value))
            .andExpect(request().sessionAttribute("lastSearchCondition", nullValue()))
            .andExpect(model().attributeExists("view"))
            .andExpect(model().attributeHasErrors("form"))
            .andExpect(view().name("item-list"));
        }
        
        static Stream<Arguments> createOkCases() {
            return Stream.of(
                    Arguments.of("page", "1"),
                    Arguments.of("kind", ItemKind.SERVICE.toString()),
                    Arguments.of("priceMin", "0"),
                    Arguments.of("priceMax", "0"),
                    Arguments.of("supplierId", UUID.randomUUID().toString()),
                    Arguments.of("status", "false"),
                    Arguments.of("sortBy", ItemSortBy.UNIT.toString()),
                    Arguments.of("sortDirection", SortDirection.DESC.toString()),
                    Arguments.of("keyword", ""),
                    Arguments.of("keyword", "あ".repeat(100))
                    );
        }
        
        static Stream<Arguments> createNgCases() {
            return Stream.of(
                    Arguments.of("page", "0"),
                    Arguments.of("priceMin", "-1"),
                    Arguments.of("priceMax", "-1"),
                    Arguments.of("supplierId", "a".repeat(35)),
                    Arguments.of("supplierId", "a".repeat(37)),
                    Arguments.of("keyword", "あ".repeat(101)));
        }
        
        @Test
        void showItem_success() throws Exception {
            doReturn(new ItemListViewDto()).when(itemService).searchItems(any());
            
            mockMvc
            .perform(get("/setting/item").with(csrf()))
            .andExpect(request().sessionAttribute("lastSearchCondition", notNullValue()))
            .andExpect(model().attributeExists("view"))
            .andExpect(view().name("item-list"));
        }
        
        @Test
        void showItem_bindingError_withLastConditon() throws Exception {
            ArgumentCaptor<ItemSearchForm> cap = ArgumentCaptor.forClass(ItemSearchForm.class);
            doReturn(new ItemListViewDto()).when(itemService).searchItems(cap.capture());
            ItemSearchForm form = new ItemSearchForm();
            form.setKeyword("test");
            form.setPage(3);
            form.setKind(ItemKind.GOODS);
            mockHttpServletRequest.getSession().setAttribute("lastSearchCondition", form);
            
            mockMvc
            .perform(get("/setting/item").with(csrf())
                    .sessionAttr("lastSearchCondition", form)
                    .param("priceMin", "-1"))
            .andExpect(model().attributeExists("view"))
            .andExpect(view().name("item-list"));
            
            ItemSearchForm captured = cap.getValue();
            assertThat(captured.getPage()).isEqualTo(3);
            assertThat(captured.getSize()).isEqualTo(2);
            assertThat(captured.getKind()).isEqualTo(ItemKind.GOODS);
            assertThat(captured.getPriceMin()).isNull();
            assertThat(captured.getPriceMax()).isNull();
            assertThat(captured.getSupplierId()).isNull();
            assertThat(captured.getStatus()).isNull();
            assertThat(captured.getKeyword()).isEqualTo("test");
            assertThat(captured.getSortBy()).isEqualTo(ItemSortBy.NAME);
            assertThat(captured.getSortDirection()).isEqualTo(SortDirection.ASC);
        }
        
        @Test
        void showItem_bindingError_withoutLastConditon() throws Exception {
            ArgumentCaptor<ItemSearchForm> cap = ArgumentCaptor.forClass(ItemSearchForm.class);
            doReturn(new ItemListViewDto()).when(itemService).searchItems(cap.capture());
            
            mockMvc
            .perform(get("/setting/item").with(csrf())
                    .param("priceMin", "-1"))
            .andExpect(model().attributeExists("view"))
            .andExpect(view().name("item-list"));
            
            ItemSearchForm captured = cap.getValue();
            assertThat(captured.getPage()).isOne();
            assertThat(captured.getSize()).isEqualTo(2);
            assertThat(captured.getKind()).isNull();
            assertThat(captured.getPriceMin()).isNull();
            assertThat(captured.getPriceMax()).isNull();
            assertThat(captured.getSupplierId()).isNull();
            assertThat(captured.getStatus()).isNull();
            assertThat(captured.getKeyword()).isNull();
            assertThat(captured.getSortBy()).isEqualTo(ItemSortBy.NAME);
            assertThat(captured.getSortDirection()).isEqualTo(SortDirection.ASC);
        }
        
        @Test
        void isPriceRangeValid_reversed() throws Exception {
            doReturn(new ItemListViewDto()).when(itemService).searchItems(any());

            MvcResult res = mockMvc
                .perform(get("/setting/item").with(csrf())
                        .param("priceMin", "200")
                        .param("priceMax", "100"))
                .andExpect(model().attributeExists("view"))
                .andExpect(model().attributeHasErrors("form"))
                .andExpect(view().name("item-list"))
                .andReturn();

            BindingResult br = (BindingResult) res.getModelAndView()
                .getModel()
                .get(BindingResult.MODEL_KEY_PREFIX + "form");

            assertThat(br.getFieldError().getDefaultMessage())
            .isEqualTo("最小価格は最大価格以下で入力してください");
        }

    }
    
    @Test
    void showItemEditForm() throws Exception {
        ItemEditViewDto dto = new ItemEditViewDto();
        dto.setName("test");
        dto.setKind(ItemKind.GOODS);
        dto.setUnitId("testUnitId");
        dto.setPrice(300);
        dto.setSupplierId("testSupp");
        dto.setDescription("testDesc");
        dto.setActive(true);
        dto.setUnitOptions(List.of(new UnitOptionDto()));
        dto.setSupplierOptions(List.of(new SupplierOptionDto(), new SupplierOptionDto()));
        doReturn(dto).when(itemService).prepareItemEditView(anyString());
        
        MvcResult res = mockMvc.perform(get("/setting/item/{itemId}/edit", "testId").with(csrf()))
            .andExpect(model().attribute("itemId", "testId"))
            .andExpect(view().name("item-edit"))
            .andReturn();
        
        ItemEditForm form = (ItemEditForm) res.getModelAndView().getModel().get("form");
        assertThat(form.getName()).isEqualTo("test");
        assertThat(form.getKind()).isEqualTo(ItemKind.GOODS);
        assertThat(form.getUnitId()).isEqualTo("testUnitId");
        assertThat(form.getPrice()).isEqualTo(300);
        assertThat(form.getSupplierId()).isEqualTo("testSupp");
        assertThat(form.getDescription()).isEqualTo("testDesc");
        assertThat(form.isActive()).isTrue();

        String itemId = (String) res.getModelAndView().getModel().get("itemId");
        assertThat(itemId).isEqualTo("testId");
        
        List<UnitOptionDto> unitOptions = (List<UnitOptionDto>) res.getModelAndView().getModel().get("unitOptions");
        assertThat(unitOptions).hasSize(1);
        
        List<SupplierOptionDto> supplierOptions = (List<SupplierOptionDto>) res.getModelAndView().getModel().get("supplierOptions");
        assertThat(supplierOptions).hasSize(2);
    }
}
