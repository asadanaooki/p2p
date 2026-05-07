package com.example.p2p.controller.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.javassist.expr.NewArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.p2p.dto.app.PurchaseRequestCreateViewDto;
import com.example.p2p.service.app.PurchaseRequestService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PurchaseRequestService purchaseRequestService;

    @Test
    void create_bindingError_details() throws Exception {
        doReturn(new PurchaseRequestCreateViewDto()).when(purchaseRequestService).prepareCreateView(anyString());
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<String, String>();
        paramsMap.add("details[0].detailInputType", null);
        paramsMap.add("details[1].detailInputType", "FREE");
        paramsMap.add("details[1].kind", null);
        paramsMap.add("details[1].itemName", "a".repeat(200));
        paramsMap.add("details[1].supplierId", "a");
        paramsMap.add("details[1].supplierName", "a".repeat(200));
        paramsMap.add("details[1].unitId", "a");
        paramsMap.add("details[1].unitName", null);
        paramsMap.add("details[1].price", "-1");
        paramsMap.add("details[1].quantity", "0");
        paramsMap.add("details[2].detailInputType", "CATALOG");
        paramsMap.add("details[2].itemId", "a".repeat(35));
        paramsMap.add("details[2].quantity", "3");

        MvcResult res = mockMvc.perform(post("/purchase-request/create").with(csrf()).params(paramsMap))
            .andExpect(model().attributeExists("view", "detailErrorMessages"))
            .andReturn();

        Map<Integer, List<String>> errorMessages = (Map<Integer, List<String>>) res.getModelAndView()
            .getModel()
            .get("detailErrorMessages");
        assertThat(errorMessages).hasSize(3);
        
        List<String> firstLineMessages = errorMessages.get(0);
        assertThat(firstLineMessages).singleElement().isEqualTo("明細情報が不正です。");
        
        List<String> secondLineMessages = errorMessages.get(1);
        assertThat(secondLineMessages).containsExactly(
                "品名は100文字以内で入力してください。",
                "種別を選択してください。",
                "仕入先情報が不正です。",
                "仕入先名は100文字以内で入力してください。",
                "単位情報が不正です。",
                "単位名は50文字以内で入力してください。",
                "単価は0円以上で入力してください。",
                "数量は1以上で入力してください。"
                );
        
        List<String> thirdLineMessages = errorMessages.get(2);
        assertThat(thirdLineMessages).singleElement().isEqualTo("カタログ明細のアイテム情報が不正です。");
        
    }

}
