package com.example.p2p.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.p2p.dto.admin.PaymentTermOptionDto;
import com.example.p2p.dto.admin.SupplierDetailDto;
import com.example.p2p.enums.DueDateType;
import com.example.p2p.form.admin.SupplierEditForm;
import com.example.p2p.service.admin.SupplierService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class SupplierControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    SupplierService supplierService;

    @Test
    void showSupplierEditForm() throws Exception {
        SupplierDetailDto dto = new SupplierDetailDto();
        dto.setSupplierName("app/test");
        dto.setEmail("test@example.com");
        dto.setPhoneNumber("0743712224");
        dto.setPostalCode("6312214");
        dto.setPrefecture("青森県");
        dto.setCity("青森市");
        dto.setStreetAddress("2-3-4");
        dto.setBuildingName("テストビル");
        dto.setPaymentTermId("33333333-3333-3333-3333-333333333332");
        dto.setActive(false);

        doReturn(dto).when(supplierService).getSupplierDetail(anyString());
        doReturn(List.of(new PaymentTermOptionDto())).when(supplierService).getPaymentTermOptions();

        MvcResult res = mockMvc.perform(get("/setting/supplier/{supplierId}/edit", "test").with(csrf()))
            .andExpect(view().name("admin/supplier-edit"))
            .andReturn();

        Map<String, Object> map = res.getModelAndView().getModel();
        SupplierEditForm form = (SupplierEditForm) map.get("form");

        assertThat(form.getName()).isEqualTo("app/test");
        assertThat(form.getEmail()).isEqualTo("test@example.com");
        assertThat(form.getPhoneNumber()).isEqualTo("0743712224");
        assertThat(form.getPostalCode()).isEqualTo("6312214");
        assertThat(form.getPrefecture()).isEqualTo("青森県");
        assertThat(form.getCity()).isEqualTo("青森市");
        assertThat(form.getStreetAddress()).isEqualTo("2-3-4");
        assertThat(form.getPaymentTermId()).isEqualTo("33333333-3333-3333-3333-333333333332");
        assertThat(form.getBuildingName()).isEqualTo("テストビル");
        assertThat(form.isStatus()).isFalse();

        assertThat((List) map.get("paymentTerms")).isNotEmpty();
        assertThat(map.get("supplierId")).isEqualTo("test");
    }

    @Nested
    class Update {

        @Test
        void update_success() throws Exception {
            doNothing().when(supplierService).update(anyString(), any());

            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
            params.add("name", "app/test");
            params.add("email", "");
            params.add("phoneNumber", "123");
            params.add("postalCode", "0123456");
            params.add("prefecture", "秋田県");
            params.add("city", "testcity");
            params.add("streetAddress", "teststreea");
            params.add("buildingName", "testbuild");
            params.add("paymentTermId", "a".repeat(36));
            params.add("status", "true");

            MvcResult res = mockMvc
                .perform(post("/setting/supplier/{supplierId}/update", "test").with(csrf()).params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/setting/supplier/" + "test"))
                .andReturn();

            assertThat(res.getFlashMap().get("successMessage")).isEqualTo("編集完了しました");
        }

        @ParameterizedTest
        @MethodSource("createOkCases")
        void create_parameter_ok(MultiValueMap<String, String> param) throws Exception {
            mockMvc.perform(post("/setting/supplier/{supplierId}/update", "test").with(csrf()).params(param))
                .andExpect(status().is3xxRedirection());
        }

        @ParameterizedTest
        @MethodSource("createNgCases")
        void create_parameter_ng(MultiValueMap<String, String> param) throws Exception {
            mockMvc.perform(post("/setting/supplier/{supplierId}/update", "test").with(csrf()).params(param))
                .andExpect(model().attributeHasErrors("form"))
                .andExpect(model().attributeExists("supplierId"))
                .andExpect(view().name("admin/supplier-edit"));
        }

        static Stream<Arguments> createOkCases() {
            // name
            MultiValueMap<String, String> okmap1 = baseParam();
            okmap1.set("name", "app/test");
            // email
            MultiValueMap<String, String> okmap2 = baseParam();
            okmap2.set("email", "test@gmail.com");
            return Stream.of(
                    // name
                    Arguments.of(okmap1),
                    // email
                    Arguments.of(okmap2));
        }

        static Stream<Arguments> createNgCases() {
            // name
            MultiValueMap<String, String> ngmap1 = baseParam();
            ngmap1.set("name", "  ");
            // email
            MultiValueMap<String, String> ngmap2 = baseParam();
            ngmap2.set("email", "sample@");
            return Stream.of(
                    // name
                    Arguments.of(ngmap1),
                    // email
                    Arguments.of(ngmap2));
        }

        private static MultiValueMap<String, String> baseParam() {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("name", "テストサプライヤー");
            params.add("email", "test@example.com");
            params.add("phoneNumber", "09012345678");
            params.add("postalCode", "1234567");
            params.add("prefecture", "大阪府");
            params.add("city", "大阪市北区");
            params.add("streetAddress", "梅田1-1-1");
            params.add("buildingName", "テストビル");
            params.add("paymentTermId", "a".repeat(36));
            params.add("status", "true");

            return params;
        }

    }
    
    @Nested
    class Create {

        @Test
        void create_success() throws Exception {
            doNothing().when(supplierService).create(any());

            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
            params.add("name", "app/test");
            params.add("email", "trest@example.com");
            params.add("phoneNumber", "123");
            params.add("postalCode", "0123456");
            params.add("prefecture", "秋田県");
            params.add("city", "testcity");
            params.add("streetAddress", "teststreea");
            params.add("buildingName", "testbuild");
            params.add("paymentTermId", "a".repeat(36));

            MvcResult res = mockMvc
                .perform(post("/setting/supplier/create", "app/test").with(csrf()).params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/setting/supplier"))
                .andReturn();

            assertThat(res.getFlashMap().get("successMessage")).isEqualTo("登録成功しました");
        }

        @ParameterizedTest
        @MethodSource("createOkCases")
        void create_parameter_ok(MultiValueMap<String, String> param) throws Exception {
            mockMvc.perform(post("/setting/supplier/create", "app/test").with(csrf()).params(param))
                .andExpect(status().is3xxRedirection());
        }

        @ParameterizedTest
        @MethodSource("createNgCases")
        void create_parameter_ng(MultiValueMap<String, String> param) throws Exception {
            mockMvc.perform(post("/setting/supplier/create").with(csrf()).params(param))
                .andExpect(model().attributeHasErrors("form"))
                .andExpect(model().attributeExists("paymentTerms"))
                .andExpect(view().name("admin/supplier-create"));
        }

        static Stream<Arguments> createOkCases() {
            // name
            MultiValueMap<String, String> okmap1 = baseParam();
            okmap1.set("name", "app/test");
            // email
            MultiValueMap<String, String> okmap2 = baseParam();
            okmap2.set("email", "test@gmail.com");
            return Stream.of(
                    // name
                    Arguments.of(okmap1),
                    // email
                    Arguments.of(okmap2));
        }

        static Stream<Arguments> createNgCases() {
            // name
            MultiValueMap<String, String> ngmap1 = baseParam();
            ngmap1.set("name", "  ");
            // email
            MultiValueMap<String, String> ngmap2 = baseParam();
            ngmap2.set("email", "sample@");
            return Stream.of(
                    // name
                    Arguments.of(ngmap1),
                    // email
                    Arguments.of(ngmap2));
        }

        private static MultiValueMap<String, String> baseParam() {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("name", "テストサプライヤー");
            params.add("email", "test@example.com");
            params.add("phoneNumber", "09012345678");
            params.add("postalCode", "1234567");
            params.add("prefecture", "大阪府");
            params.add("city", "大阪市北区");
            params.add("streetAddress", "梅田1-1-1");
            params.add("buildingName", "テストビル");
            params.add("paymentTermId", "a".repeat(36));
            params.add("status", "true");

            return params;
        }

    }
    
    

}
