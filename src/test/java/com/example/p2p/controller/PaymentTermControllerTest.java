package com.example.p2p.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.p2p.enums.DueDateType;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.service.PaymentTermService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentTermControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PaymentTermService paymentTermService;

    @Nested
    class Update {

        @ParameterizedTest
        @MethodSource("createOkCases")
        void update_parameter_ok(String name, int days, DueDateType type, boolean active) throws Exception {
            mockMvc
                .perform(post("/setting/payment-term/{paymentTermId}/update", "testId").with(csrf())
                    .param("name", name)
                    .param("days", String.valueOf(days))
                    .param("dueDateType", type.toString())
                    .param("active", String.valueOf(active)))
                .andExpect(status().is3xxRedirection());
        }

        @ParameterizedTest
        @MethodSource("createNgCases")
        void update_parameter_ng(String name, int days, DueDateType type, boolean active) throws Exception {
             mockMvc
                .perform(post("/setting/payment-term/{paymentTermId}/update", "testId").with(csrf())
                    .param("name", name)
                    .param("days", String.valueOf(days))
                    .param("dueDateType", type.toString())
                    .param("active", String.valueOf(active)))
                .andExpect(model().attributeHasErrors("form"))
                .andExpect(view().name("payment-term-edit"));
        }

        static Stream<Arguments> createOkCases() {
            return Stream.of(
                    // name
                    Arguments.of("あ", 20, DueDateType.NET_DAYS, true),
                    Arguments.of("あ".repeat(50), 20, DueDateType.NET_DAYS, true),

                    // days
                    Arguments.of("test", 1, DueDateType.NET_DAYS, true),
                    // assertTrue
                    Arguments.of("test", 5, DueDateType.NET_DAYS, true),
                    Arguments.of("test", 31, DueDateType.NEXT_MONTH_DAY, true));
        }

        static Stream<Arguments> createNgCases() {
            return Stream.of(
                    // name
                    Arguments.of(" ", 20, DueDateType.NET_DAYS, true), 
                    Arguments.of("", 20, DueDateType.NET_DAYS, true),
                    Arguments.of("あ".repeat(51), 20, DueDateType.NET_DAYS, true),
                    // days
                    Arguments.of("test", 0, DueDateType.NET_DAYS, true),
                    // assertTrue
                    Arguments.of("test", 32, DueDateType.THIS_MONTH_DAY, true));
        }

        @Test
        void update_success() throws Exception {
            doNothing().when(paymentTermService).update(anyString(), any());

            MvcResult res = mockMvc
                .perform(post("/setting/payment-term/{paymentTermId}/update", "testId").with(csrf())
                    .param("name", "test")
                    .param("days", "30")
                    .param("dueDateType", "THIS_MONTH_DAY")
                    .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/setting/payment-term/testId"))
                .andReturn();

            assertThat(res.getFlashMap().get("successMessage")).isEqualTo("編集完了しました");
        }

        @Test
        void update_duplicate() throws Exception {
            doThrow(BusinessException.class).when(paymentTermService).update(anyString(), any());

            MvcResult res = mockMvc
                .perform(post("/setting/payment-term/{paymentTermId}/update", "testId").with(csrf())
                    .param("name", "test")
                    .param("days", "30")
                    .param("dueDateType", "THIS_MONTH_DAY")
                    .param("active", "true"))
                .andExpect(view().name("payment-term-edit"))
                .andReturn();
            Map<String, Object> model = res.getModelAndView().getModel();
            BindingResult br = (BindingResult) model.get(BindingResult.MODEL_KEY_PREFIX + "form");
            FieldError fe = br.getFieldError("name");
            assertThat(fe.getField()).isEqualTo("name");
            assertThat(fe.getCode()).isEqualTo("duplicate");
            assertThat(fe.getDefaultMessage()).isEqualTo("既に登録されています");

            assertThat(model.get("form")).isNotNull();
        }

    }

}
