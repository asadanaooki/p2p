package com.example.p2p.controller.app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.p2p.dto.InitialPasswordSetupViewDto;
import com.example.p2p.service.app.AccountService;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AccountService accountService;

    @Nested
    class Accept {

        @Test
        void accept_bindingError() throws Exception {
            InitialPasswordSetupViewDto dto = new InitialPasswordSetupViewDto();
            dto.setFirstName("");
            dto.setLastName("");
            doReturn(dto).when(accountService).getInvitedUserInfo(any());
            mockMvc.perform(post("/account/initial-password-setup").with(csrf()).param("token", ""))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeHasErrors("form"))
                .andExpect(view().name("app/initial-password-setup"));
        }

        @Test
        void accept_success() throws Exception {
            doReturn("siotan0926@gmail.com").when(accountService).acceptInvitation(any());
            mockMvc
                .perform(post("/account/initial-password-setup").with(csrf())
                    .param("token", "aaa")
                    .param("password", "softeni0926")
                    .param("confirmPassword", "softeni0926"))
                .andExpect(authenticated().withUsername("36a1d5d9-15b8-45d5-8ae7-607244bbe36e"));
        }

        @ParameterizedTest
        @MethodSource("createOkCases")
        void accept_param_ok(String token, String password, String confirmPassword) throws Exception {
            doReturn("siotan0926@gmail.com").when(accountService).acceptInvitation(any());
            mockMvc
                .perform(post("/account/initial-password-setup").with(csrf())
                    .param("token", token)
                    .param("password", password)
                    .param("confirmPassword", confirmPassword))
                .andExpect(status().is3xxRedirection());
        }

        @ParameterizedTest
        @MethodSource("createNgCases")
        void accept_param_ng(String token, String password, String confirmPassword, String expField) throws Exception {
            InitialPasswordSetupViewDto dto = new InitialPasswordSetupViewDto();
            dto.setFirstName("");
            dto.setLastName("");
            doReturn(dto).when(accountService).getInvitedUserInfo(any());
            mockMvc
                .perform(post("/account/initial-password-setup").with(csrf())
                    .param("token", token)
                    .param("password", password)
                    .param("confirmPassword", confirmPassword))
                .andExpect(model().attributeHasFieldErrors("form", expField))
                .andExpect(view().name("app/initial-password-setup"));
        }

        static Stream<Arguments> createOkCases() {
            return Stream.of(
                    // token
                    Arguments.of("abcd", "12345678abc", "12345678abc"),
                    // password
                    Arguments.of("abcd", "12345678abc", "12345678abc"),
                    Arguments.of("abcd", "a".repeat(8), "a".repeat(8)),
                    Arguments.of("abcd", "a".repeat(60), "a".repeat(60)),
                    Arguments.of("abcd", "ag23f%$Q", "ag23f%$Q"));
        }

        static Stream<Arguments> createNgCases() {
            return Stream.of(
                    // token
                    Arguments.of(null, "12345678abc", "12345678abc", "token"),
                    Arguments.of("", "12345678abc", "12345678abc", "token"),

                    // password
                    Arguments.of("abcd", null, null, "password"),
                    Arguments.of("abcd", "", "", "password"),
                    Arguments.of("abcd", "a".repeat(7), "a".repeat(7), "password"),
                    Arguments.of("abcd", "a".repeat(61), "a".repeat(61), "password"),
                    Arguments.of("abcd", "ag23ｶf%$Q", "ag23ｶf%$Q", "password"),
                    Arguments.of("abcd", "ag23あf%$Q", "ag23あf%$Q", "password"),
                    Arguments.of("abcd", "app/test", "test2", "password"),

                    // confirmPassword
                    Arguments.of("abcd", null, null, "confirmPassword"),
                    Arguments.of("abcd", "", "", "confirmPassword"),
                    Arguments.of("abcd", "a".repeat(7), "a".repeat(7), "confirmPassword"),
                    Arguments.of("abcd", "a".repeat(61), "a".repeat(61), "confirmPassword"),
                    Arguments.of("abcd", "ag23ｶf%$Q", "ag23ｶf%$Q", "confirmPassword"),
                    Arguments.of("abcd", "ag23あf%$Q", "ag23あf%$Q", "confirmPassword"),
                    Arguments.of("abcd", "app/test", "test2", "confirmPassword"));
        }

    }

}
