package com.example.p2p.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.p2p.exception.BusinessException;
import com.example.p2p.service.UnitService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class UnitControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UnitService unitService;

    @Test
    void create_success() throws Exception {
        doNothing().when(unitService).create(anyString());

        MvcResult res = mockMvc.perform(post("/setting/unit/create").with(csrf()).param("name", "test"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/setting/unit"))
            .andReturn();

        assertThat(res.getFlashMap().get("successMessage")).isEqualTo("登録成功しました");
    }

    @Test
    void create_duplicate() throws Exception {
        doThrow(BusinessException.class).when(unitService).create(anyString());

        MvcResult res = mockMvc.perform(post("/setting/unit/create").with(csrf()).param("name", "test"))
            .andExpect(view().name("unit-create"))
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
