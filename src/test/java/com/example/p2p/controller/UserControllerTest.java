package com.example.p2p.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.p2p.dto.ItemListViewDto;
import com.example.p2p.dto.UserListViewDto;
import com.example.p2p.enums.ItemSortBy;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.enums.UserSortBy;
import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    
    @MockitoBean
    UserService userService;
    
    @Test
    void showUserList_success() throws Exception {
        doReturn(new UserListViewDto()).when(userService).searchUsers(any());
        
        mockMvc
        .perform(get("/setting/user").with(csrf()))
        .andExpect(request().sessionAttribute("lastSearchCondition", notNullValue()))
        .andExpect(model().attributeExists("view"))
        .andExpect(view().name("user-list"));
    }
    
    @Test
    void showItem_bindingError_withLastConditon() throws Exception {
        ArgumentCaptor<UserSearchForm> cap = ArgumentCaptor.forClass(UserSearchForm.class);
        doReturn(new UserListViewDto()).when(userService).searchUsers(cap.capture());
        UserSearchForm form = new UserSearchForm();
        form.setKeyword("test");
        form.setPage(3);
        
        mockMvc
        .perform(get("/setting/user").with(csrf())
                .sessionAttr("lastSearchCondition", form)
                .param("roleId", "a".repeat(37)))
        .andExpect(model().attributeExists("view"))
        .andExpect(view().name("user-list"));
        
        UserSearchForm captured = cap.getValue();
        assertThat(captured.getPage()).isEqualTo(3);
        assertThat(captured.getSize()).isEqualTo(2);
        assertThat(captured.getRoleId()).isNull();
        assertThat(captured.getStatus()).isNull();
        assertThat(captured.getKeyword()).isEqualTo("test");
        assertThat(captured.getSortBy()).isEqualTo(UserSortBy.NAME);
        assertThat(captured.getSortDirection()).isEqualTo(SortDirection.ASC);
    }
    
    @Test
    void showItem_bindingError_withoutLastConditon() throws Exception {
        ArgumentCaptor<UserSearchForm> cap = ArgumentCaptor.forClass(UserSearchForm.class);
        doReturn(new UserListViewDto()).when(userService).searchUsers(cap.capture());
        
        mockMvc
        .perform(get("/setting/user").with(csrf())
                .param("roleId", "a".repeat(37)))
        .andExpect(model().attributeExists("view"))
        .andExpect(view().name("user-list"));
        
        UserSearchForm captured = cap.getValue();
        assertThat(captured.getPage()).isEqualTo(1);
        assertThat(captured.getSize()).isEqualTo(2);
        assertThat(captured.getRoleId()).isNull();
        assertThat(captured.getStatus()).isNull();
        assertThat(captured.getKeyword()).isNull();
        assertThat(captured.getSortBy()).isEqualTo(UserSortBy.NAME);
        assertThat(captured.getSortDirection()).isEqualTo(SortDirection.ASC);
    }
}
