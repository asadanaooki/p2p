package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.p2p.dto.admin.UserDetailDto;
import com.example.p2p.dto.admin.UserListRowDto;
import com.example.p2p.entity.ApprovalStepApproverExample;
import com.example.p2p.entity.ApprovalTaskExample;
import com.example.p2p.entity.PurchaseRequestDetailExample;
import com.example.p2p.entity.PurchaseRequestExample;
import com.example.p2p.entity.Users;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.enums.UserSortBy;
import com.example.p2p.form.admin.UserSearchForm;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersMapperCustomTest {

    @Autowired
    UsersMapperCustom usersMapperCustom;

    @Autowired
    UsersMapper usersMapper;
    
    @Autowired
    PurchaseRequestMapper purchaseRequestMapper;

    @Autowired
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    @Autowired
    ApprovalStepApproverMapper approvalStepApproverMapper;
    
    @Autowired
    ApprovalTaskMapper approvalTaskMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Nested
    class SelectUsers {

        @Test
        void selectUsers_allCondition() {
            UserSearchForm form = new UserSearchForm();
            form.setRoleId("95daf9ce-b599-41e0-ae0d-f4687e718a2c");
            form.setStatus(true);

            List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

            assertThat(actual).hasSize(1);

            UserListRowDto first = actual.get(0);
            assertThat(first.getUserId()).isEqualTo("36a1d5d9-15b8-45d5-8ae7-607244bbe36e");
            assertThat(first.getLastName()).isEqualTo("山田");
            assertThat(first.getFirstName()).isEqualTo("太郎");
            assertThat(first.getEmail()).isEqualTo("siotan0926@gmail.com");
            assertThat(first.getRoleId()).isEqualTo("95daf9ce-b599-41e0-ae0d-f4687e718a2c");
            assertThat(first.getRoleName()).isEqualTo("管理者");
            assertThat(first.isActive()).isTrue();
        }

        @Test
        void selectUsers_noCondition() {
            UserSearchForm form = new UserSearchForm();
            List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);
            UserListRowDto first = actual.get(0);
            assertThat(first.getUserId()).isEqualTo("169f1e17-619f-45bf-b6dc-8faed08c404c");
            assertThat(first.getLastName()).isEqualTo("佐藤");
            assertThat(first.getFirstName()).isEqualTo("花子");
            assertThat(first.getLastNameKana()).isEqualTo("サトウ");
            assertThat(first.getFirstNameKana()).isEqualTo("ハナコ");
            assertThat(first.getEmail()).isEqualTo("sato.hanako@example.com");
            assertThat(first.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
            assertThat(first.getRoleName()).isEqualTo("マネージャー");

        }

        @Nested
        class Filter {

            @ParameterizedTest
            @MethodSource("createFilterCaces")
            void selectUsers_filter(UserSearchForm form, int expected) {
                Users u = new Users();
                u.setUserId("6fe99043-cbd1-49c0-96d4-c156c58a8e60");
                u.setIsActive(false);
                usersMapper.updateByPrimaryKeySelective(u);
                form.setSize(100);

                List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                assertThat(actual).hasSize(expected);
            }

            static Stream<Arguments> createFilterCaces() {
                UserSearchForm roleForm = new UserSearchForm();
                roleForm.setRoleId("71dc166d-5059-4fbc-8bca-71d0c5dc2526");
                UserSearchForm statusForm = new UserSearchForm();
                statusForm.setStatus(false);

                return Stream.of(Arguments.of(roleForm, 1), Arguments.of(statusForm, 1));
            }

        }

        @Nested
        class Search {

            @BeforeEach
            void setup() {
                jdbcTemplate.update("delete from purchase_order_line_allocation");
                jdbcTemplate.update("delete from purchase_order_line");
                jdbcTemplate.update("delete from purchase_request_purchase_order");
                jdbcTemplate.update("delete from purchase_order");
                purchaseRequestDetailMapper.deleteByExample(new PurchaseRequestDetailExample());
                purchaseRequestMapper.deleteByExample(new PurchaseRequestExample());
                approvalStepApproverMapper.deleteByExample(new ApprovalStepApproverExample());
                approvalTaskMapper.deleteByExample(new ApprovalTaskExample());
                usersMapper.deleteByExample(new UsersExample());

                Users u = new Users();
                u.setUserId("3f7c2a91-5d84-4b6f-9a21-7c8e3f1d6b42");
                u.setLastName("dummyLastName");
                u.setFirstName("dummyFirstName");
                u.setFirstNameKana("dummyFirstNameKana");
                u.setLastNameKana("dummyLastNameKana");
                u.setEmail("dummyEmail");
                u.setPasswordHash("");
                u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");

                usersMapper.insertSelective(u);
            }

            @ParameterizedTest
            @ValueSource(strings = { "朝田直樹", "アサダナオ" })
            void selectUsers_fullNameKeyword(String keyword) {
                Users u = new Users();
                u.setUserId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                u.setLastName("朝田");
                u.setFirstName("直樹");
                u.setFirstNameKana("ナオキ");
                u.setLastNameKana("アサダ");
                u.setEmail("dummyEmail2");
                u.setPasswordHash("");
                u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                usersMapper.insertSelective(u);

                UserSearchForm form = new UserSearchForm();
                form.setKeyword(keyword);
                form.setSize(100);
                List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                assertThat(actual).hasSize(1);
                assertThat(actual.get(0).getUserId()).isEqualTo("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
            }

            @Nested
            class Normalization {

                @Test
                void selectUsers_toLower() {
                    Users u = new Users();
                    u.setUserId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    u.setLastName("ASAdA");
                    u.setFirstName("dummyFirstName");
                    u.setFirstNameKana("dummyFirstNameKana");
                    u.setLastNameKana("dummyLastNameKana");
                    u.setEmail("dummyEmail2");
                    u.setPasswordHash("");
                    u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                    usersMapper.insertSelective(u);

                    UserSearchForm form = new UserSearchForm();
                    form.setKeyword("asada");
                    form.setSize(100);
                    List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                    assertThat(actual).hasSize(1);
                    assertThat(actual.get(0).getUserId()).isEqualTo("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                }

                @Test
                void selectUsers_fullToHalf() {
                    Users u = new Users();
                    u.setUserId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    u.setLastName("dummyLastName");
                    u.setFirstName("Ｎaoｋi７ff");
                    u.setFirstNameKana("dummyFirstNameKana");
                    u.setLastNameKana("dummyLastNameKana");
                    u.setEmail("dummyEmail2");
                    u.setPasswordHash("");
                    u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                    usersMapper.insertSelective(u);

                    UserSearchForm form = new UserSearchForm();
                    form.setKeyword("naoki7");
                    form.setSize(100);
                    List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                    assertThat(actual).hasSize(1);
                    assertThat(actual.get(0).getUserId()).isEqualTo("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                }

                @Test
                void selectUsers_hiraganaToKatakana() {
                    Users u = new Users();
                    u.setUserId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    u.setLastName("dummyLastName");
                    u.setFirstName("dummyFirstName");
                    u.setFirstNameKana("いさむ");
                    u.setLastNameKana("dummyLastNameKana");
                    u.setEmail("dummyEmail2");
                    u.setPasswordHash("");
                    u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                    usersMapper.insertSelective(u);

                    UserSearchForm form = new UserSearchForm();
                    form.setKeyword("イサ");
                    form.setSize(100);
                    List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                    assertThat(actual).hasSize(1);
                    assertThat(actual.get(0).getUserId()).isEqualTo("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                }

                @Test
                void selectUsers_halfKatakanaToFullKatakana() {
                    Users u = new Users();
                    u.setUserId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    u.setLastName("dummyLastName");
                    u.setFirstName("dummyFirstName");
                    u.setFirstNameKana("dummyFirstNameKana");
                    u.setLastNameKana("ｱｻﾀﾞ");
                    u.setEmail("dummyEmail2");
                    u.setPasswordHash("");
                    u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                    usersMapper.insertSelective(u);

                    UserSearchForm form = new UserSearchForm();
                    form.setKeyword("アサダ");
                    form.setSize(100);
                    List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                    assertThat(actual).hasSize(1);
                    assertThat(actual.get(0).getUserId()).isEqualTo("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                }

                @ParameterizedTest
                @ValueSource(strings = { " ", "　", "(", ")", "[", "]" })
                void selectUsers_ignoreSymbol(String symbol) {
                    Users u = new Users();
                    u.setUserId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    u.setLastName("朝田" + symbol + "アサダ");
                    u.setFirstName("dummyFirstName");
                    u.setFirstNameKana("dummyFirstNameKana");
                    u.setLastNameKana("dummyLastNameKana");
                    u.setEmail("dummyEmail2");
                    u.setPasswordHash("");
                    u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                    usersMapper.insertSelective(u);

                    UserSearchForm form = new UserSearchForm();
                    form.setKeyword("田アサ");
                    form.setSize(100);
                    List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                    assertThat(actual).hasSize(1);
                    assertThat(actual.get(0).getUserId()).isEqualTo("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                }

                @Test
                void selectUsers_matchesMultiColumns() {
                    Users u = new Users();
                    u.setUserId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    u.setLastName("朝田");
                    u.setFirstName("直樹");
                    u.setFirstNameKana("アサダナオキ");
                    u.setLastNameKana("あさだなおき");
                    u.setEmail("dummyEmail2");
                    u.setPasswordHash("");
                    u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                    usersMapper.insertSelective(u);

                    UserSearchForm form = new UserSearchForm();
                    form.setKeyword("アサダ");
                    form.setSize(100);
                    List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                    assertThat(actual).hasSize(1);
                    assertThat(actual.get(0).getUserId()).isEqualTo("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                }

            }

        }

        @Nested
        class Sort {

            @BeforeEach
            void setup() {
                jdbcTemplate.update("delete from purchase_order_line_allocation");
                jdbcTemplate.update("delete from purchase_order_line");
                jdbcTemplate.update("delete from purchase_request_purchase_order");
                jdbcTemplate.update("delete from purchase_order");
                purchaseRequestDetailMapper.deleteByExample(new PurchaseRequestDetailExample());
                purchaseRequestMapper.deleteByExample(new PurchaseRequestExample());
                approvalStepApproverMapper.deleteByExample(new ApprovalStepApproverExample());
                approvalTaskMapper.deleteByExample(new ApprovalTaskExample());
                usersMapper.deleteByExample(new UsersExample());
                // Name Desc
                Users u1 = new Users();
                u1.setUserId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                u1.setLastName("渡辺");
                u1.setFirstName("加奈子");
                u1.setFirstNameKana("カナコ");
                u1.setLastNameKana("ワタナベ");
                u1.setEmail("watanabe@example.com");
                u1.setPasswordHash("");
                u1.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                u1.setIsActive(true);
                usersMapper.insertSelective(u1);

                // Email Asc
                Users u2 = new Users();
                u2.setUserId("3f8a1c72-6b54-4d19-9e2a-7c4f1b8d2a63");
                u2.setLastName("朝田");
                u2.setFirstName("直樹");
                u2.setFirstNameKana("ナオキ");
                u2.setLastNameKana("アサダ");
                u2.setEmail("asada@example.com");
                u2.setPasswordHash("");
                u2.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                u2.setIsActive(true);
                usersMapper.insertSelective(u2);

                // Role Desc
                Users u3 = new Users();
                u3.setUserId("a7d93e10-2f6c-4b85-8a31-5e9c2d74f1ab");
                u3.setLastName("仲野");
                u3.setFirstName("拓夢");
                u3.setFirstNameKana("タクム");
                u3.setLastNameKana("ナカノ");
                u3.setEmail("nakano@example.com");
                u3.setPasswordHash("");
                u3.setRoleId("95daf9ce-b599-41e0-ae0d-f4687e718a2c");
                u3.setIsActive(true);
                usersMapper.insertSelective(u3);

                // Status Asc
                Users u4 = new Users();
                u4.setUserId("9b6f2d31-8c47-4a95-b1de-6f3a9c2d7e18");
                u4.setLastName("松本");
                u4.setFirstName("知也");
                u4.setFirstNameKana("トモヤ");
                u4.setLastNameKana("マツモト");
                u4.setEmail("matumoto@example.com");
                u4.setPasswordHash("");
                u4.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
                u4.setIsActive(false);
                usersMapper.insertSelective(u4);
            }

            @ParameterizedTest
            @MethodSource("createSortCases")
            void selectUsers_sort(UserSortBy sortBy, SortDirection direction, String expectedUserId) {
                UserSearchForm form = new UserSearchForm();
                form.setSortBy(sortBy);
                form.setSortDirection(direction);
                List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

                assertThat(actual).hasSize(2);
                if (sortBy == UserSortBy.NAME) {
                    assertThat(actual.get(0).getUserId()).isEqualTo(expectedUserId);
                } else {
                    assertThat(actual).extracting(UserListRowDto::getUserId).contains(expectedUserId);
                }
            }

            static Stream<Arguments> createSortCases() {
                return Stream.of(
                        Arguments.of(UserSortBy.NAME, SortDirection.DESC, "6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134"),
                        Arguments.of(UserSortBy.EMAIL, SortDirection.ASC, "3f8a1c72-6b54-4d19-9e2a-7c4f1b8d2a63"),
                        Arguments.of(UserSortBy.ROLE, SortDirection.DESC, "a7d93e10-2f6c-4b85-8a31-5e9c2d74f1ab"),
                        Arguments.of(UserSortBy.STATUS, SortDirection.ASC, "9b6f2d31-8c47-4a95-b1de-6f3a9c2d7e18"));
            }

        }

    }

    @Test
    void selectUserDetail() {
        UserDetailDto actual = usersMapperCustom.selectUserDetail("169f1e17-619f-45bf-b6dc-8faed08c404c");

        assertThat(actual.getLastName()).isEqualTo("佐藤");
        assertThat(actual.getFirstName()).isEqualTo("花子");
        assertThat(actual.getLastNameKana()).isEqualTo("サトウ");
        assertThat(actual.getFirstNameKana()).isEqualTo("ハナコ");
        assertThat(actual.getEmail()).isEqualTo("sato.hanako@example.com");
        assertThat(actual.getRoleName()).isEqualTo("マネージャー");
        assertThat(actual.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
    }

}
