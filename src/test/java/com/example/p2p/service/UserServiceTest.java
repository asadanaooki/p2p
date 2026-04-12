package com.example.p2p.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.UserListViewDto;
import com.example.p2p.entity.Users;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.form.UserUpsertForm;
import com.example.p2p.mapper.UsersMapper;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Nested
    class SearchItems {

        @Autowired
        UserService userService;

        @Autowired
        UsersMapper usersMapper;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Nested
        class SearchUsers {

            @BeforeEach
            void setup() {
                usersMapper.deleteByExample(new UsersExample());
            }

            @Test
            void searchUsers_exists() {
                StringBuilder sb = new StringBuilder();
                sb.append("0".repeat(8));
                sb.append("-");
                sb.append("0".repeat(4));
                sb.append("-");
                sb.append("0".repeat(4));
                sb.append("-");
                sb.append("0".repeat(4));
                sb.append("-");
                sb.append("0".repeat(12));
                String firstUUID = sb.toString();

                for (int i = 0; i < 50; i++) {
                    Users user = new Users();
                    String replace = String.format("%02d", i);
                    String uuid = firstUUID.substring(0, firstUUID.length() - 2) + replace;

                    user.setUserId(uuid);
                    user.setLastName("la" + i);
                    user.setFirstName("fi" + i);
                    user.setLastNameKana("lakana" + i);
                    user.setFirstNameKana("fikana" + i);
                    user.setEmail("email" + i);
                    user.setPasswordHash("pass" + i);
                    user.setRoleId("71dc166d-5059-4fbc-8bca-71d0c5dc2526");
                    usersMapper.insertSelective(user);
                }

                UserSearchForm form = new UserSearchForm();
                form.setPage(2);
                form.setSize(7);
                UserListViewDto actual = userService.searchUsers(form);

                assertThat(actual.getUsers()).hasSize(7);
                assertThat(actual.getRoleOptions()).hasSize(3);
                assertThat(actual.getCurrentPage()).isEqualTo(2);
                assertThat(actual.getPageNumberList()).isEqualTo(List.of(1, 2, 3, 4, 5));

            }

            @Test
            void searchUsers_notFound() {
                UserListViewDto actual = userService.searchUsers(new UserSearchForm());

                assertThat(actual.getUsers()).isEmpty();
                assertThat(actual.getCurrentPage()).isEqualTo(1);
                assertThat(actual.getPageNumberList()).isEmpty();
            }

        }

        @Nested
        class Create {

            UserUpsertForm form;

            @BeforeEach
            void setup() {
                form = new UserUpsertForm();
                form.setLastName("高木");
                form.setFirstName("太郎");
                form.setLastNameKana("タカギ");
                form.setFirstNameKana("タロウ");
                form.setEmail("takagi@example.com");
                form.setPassword("password123");
                form.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
            }

            @Test
            void create_duplicate() {
                form.setEmail("sato.hanako@example.com");

                assertThatThrownBy(() -> userService.create(form)).isInstanceOf(BusinessException.class);
            }

            @Test
            void create_success() {
                userService.create(form);

                UsersExample ex = new UsersExample();
                ex.createCriteria().andEmailEqualTo("takagi@example.com");
                Users created = usersMapper.selectByExample(ex).get(0);

                assertThat(created.getUserId()).isNotBlank();
                assertThat(created.getLastName()).isEqualTo("高木");
                assertThat(created.getFirstName()).isEqualTo("太郎");
                assertThat(created.getLastNameKana()).isEqualTo("タカギ");
                assertThat(created.getFirstNameKana()).isEqualTo("タロウ");
                assertThat(created.getEmail()).isEqualTo("takagi@example.com");
                assertThat(created.getPasswordHash().length()).isEqualTo(60);
                assertThat(created.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
                assertThat(created.getIsActive()).isTrue();
                assertThat(created.getCreatedAt()).isNotNull();
                assertThat(created.getUpdatedAt()).isNotNull();
                assertThat(passwordEncoder.matches("password123", created.getPasswordHash())).isTrue();
            }

        }

    }

}
