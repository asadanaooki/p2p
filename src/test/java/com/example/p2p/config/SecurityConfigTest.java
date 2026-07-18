package com.example.p2p.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.p2p.service.app.CustomUserDetailsService;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @Test
    void loginSuccess_removesLastLoginUsername() throws Exception {
        doReturn(User.withUsername("user-id")
            .password(passwordEncoder.encode("password"))
            .authorities("PR_CREATE")
            .build())
            .when(customUserDetailsService).loadUserByUsername("user@example.com");

        MvcResult result = mockMvc.perform(post("/login")
                .with(csrf())
                .sessionAttr("LAST_LOGIN_USERNAME", "failed@example.com")
                .param("username", "user@example.com")
                .param("password", "password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/test"))
            .andExpect(authenticated().withUsername("user-id"))
            .andReturn();

        assertThat(result.getRequest().getSession(false).getAttribute("LAST_LOGIN_USERNAME")).isNull();
    }
}
