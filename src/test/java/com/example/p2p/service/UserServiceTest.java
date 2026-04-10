package com.example.p2p.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.UserListViewDto;
import com.example.p2p.entity.Users;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.form.UserSearchForm;
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

}
