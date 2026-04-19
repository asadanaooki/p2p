package com.example.p2p.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.function.Consumer;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.p2p.dto.UserListViewDto;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.enums.UserSortBy;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.form.UserUpsertForm;
import com.example.p2p.service.admin.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Nested
    class ShowUserList {

        @Test
        void showUserList_success() throws Exception {
            doReturn(new UserListViewDto()).when(userService).searchUsers(any());

            mockMvc.perform(get("/setting/user").with(csrf()))
                .andExpect(request().sessionAttribute("lastSearchCondition", notNullValue()))
                .andExpect(model().attributeExists("view"))
                .andExpect(view().name("user-list"));
        }

        @Test
        void showUserList_bindingError_withLastConditon() throws Exception {
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
        void showUserList_bindingError_withoutLastConditon() throws Exception {
            ArgumentCaptor<UserSearchForm> cap = ArgumentCaptor.forClass(UserSearchForm.class);
            doReturn(new UserListViewDto()).when(userService).searchUsers(cap.capture());

            mockMvc.perform(get("/setting/user").with(csrf()).param("roleId", "a".repeat(37)))
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

    @Nested
    class Create {

        static String email254 = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(57)
                + ".com";
        static String email255 = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(58)
                + ".com";

        @ParameterizedTest
        @MethodSource("createNewOkCaces")
        void create_parameter_ok(Consumer<UserUpsertForm> consumer) throws Exception {
            doReturn("testId").when(userService).create(any());
            UserUpsertForm form = baseForm();
            consumer.accept(form);

            mockMvc
                .perform(post("/setting/user/create").with(csrf())
                    .param("lastName", form.getLastName())
                    .param("firstName", form.getFirstName())
                    .param("lastNameKana", form.getLastNameKana())
                    .param("firstNameKana", form.getFirstNameKana())
                    .param("email", form.getEmail())
                    .param("roleId", form.getRoleId()))
                .andExpect(redirectedUrl("/setting/user/" + "testId"));
        }

        @ParameterizedTest
        @MethodSource("createNewNgCaces")
        void create_parameter_ng(String field, Consumer<UserUpsertForm> consumer) throws Exception {
            doReturn(new UserListViewDto()).when(userService).searchUsers(any());
            UserUpsertForm form = baseForm();
            consumer.accept(form);

            mockMvc
                .perform(post("/setting/user/create").with(csrf())
                    .param("lastName", form.getLastName())
                    .param("firstName", form.getFirstName())
                    .param("lastNameKana", form.getLastNameKana())
                    .param("firstNameKana", form.getFirstNameKana())
                    .param("email", form.getEmail())
                    .param("roleId", form.getRoleId()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", field))
                .andExpect(view().name("user-create"));
        }

        static Stream<Arguments> createNewNgCaces() {
            return Stream.of(
                    // lastName
                    Arguments.of("lastName", (Consumer<UserUpsertForm>) f -> f.setLastName(null)),
                    Arguments.of("lastName", (Consumer<UserUpsertForm>) f -> f.setLastName("")),
                    Arguments.of("lastName", (Consumer<UserUpsertForm>) f -> f.setLastName("朝".repeat(51))),
                    // firstName
                    Arguments.of("firstName", (Consumer<UserUpsertForm>) f -> f.setFirstName(null)),
                    Arguments.of("firstName", (Consumer<UserUpsertForm>) f -> f.setFirstName(" ")),
                    Arguments.of("firstName", (Consumer<UserUpsertForm>) f -> f.setFirstName("直".repeat(51))),
                    // lastNameKana
                    Arguments.of("lastNameKana", (Consumer<UserUpsertForm>) f -> f.setLastNameKana(null)),
                    Arguments.of("lastNameKana", (Consumer<UserUpsertForm>) f -> f.setLastNameKana(" ")),
                    Arguments.of("lastNameKana", (Consumer<UserUpsertForm>) f -> f.setLastNameKana("ア".repeat(51))),
                    // firstNameKana
                    Arguments.of("firstNameKana", (Consumer<UserUpsertForm>) f -> f.setFirstNameKana(null)),
                    Arguments.of("firstNameKana", (Consumer<UserUpsertForm>) f -> f.setFirstNameKana(" ")),
                    Arguments.of("firstNameKana", (Consumer<UserUpsertForm>) f -> f.setFirstNameKana("ナ".repeat(51))),
                    // email
                    Arguments.of("email", (Consumer<UserUpsertForm>) f -> f.setEmail(null)),
                    Arguments.of("email", (Consumer<UserUpsertForm>) f -> f.setEmail("　")),
                    Arguments.of("email", (Consumer<UserUpsertForm>) f -> f.setEmail(email255)),
                    Arguments.of("email", (Consumer<UserUpsertForm>) f -> f.setEmail("axc@")),
                    // roleId
                    Arguments.of("roleId", (Consumer<UserUpsertForm>) f -> f.setRoleId(null)),
                    Arguments.of("roleId", (Consumer<UserUpsertForm>) f -> f.setRoleId("a".repeat(35))),
                    Arguments.of("roleId", (Consumer<UserUpsertForm>) f -> f.setRoleId("a".repeat(37))));
        }

        static Stream<Arguments> createNewOkCaces() {
            return Stream.of(
                    // lastName
                    Arguments.of((Consumer<UserUpsertForm>) f -> f.setLastName("朝".repeat(50))),
                    // firstName
                    Arguments.of((Consumer<UserUpsertForm>) f -> f.setFirstName("直".repeat(50))),
                    // lastNameKana
                    Arguments.of((Consumer<UserUpsertForm>) f -> f.setLastNameKana("ア".repeat(50))),
                    // firstNameKana
                    Arguments.of((Consumer<UserUpsertForm>) f -> f.setFirstNameKana("ナ".repeat(50))),
                    // email
                    Arguments.of((Consumer<UserUpsertForm>) f -> f.setEmail(email254)),
                    // roleId
                    Arguments.of((Consumer<UserUpsertForm>) f -> f.setRoleId("a".repeat(36))));
        }

        UserUpsertForm baseForm() {
            UserUpsertForm form = new UserUpsertForm();
            form.setLastName("松井");
            form.setFirstName("裕樹");
            form.setLastNameKana("マツイ");
            form.setFirstNameKana("ヒロキ");
            form.setEmail("matui@example.com");
            form.setRoleId("a".repeat(36));
            return form;

        }

    }

}
