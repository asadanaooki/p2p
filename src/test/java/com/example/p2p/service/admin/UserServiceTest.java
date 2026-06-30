package com.example.p2p.service.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.admin.UserDetailDto;
import com.example.p2p.dto.admin.UserListViewDto;
import com.example.p2p.entity.ApprovalStepApproverExample;
import com.example.p2p.entity.ApprovalTaskExample;
import com.example.p2p.entity.PurchaseRequestDetailExample;
import com.example.p2p.entity.PurchaseRequestExample;
import com.example.p2p.entity.UserInvitationToken;
import com.example.p2p.entity.UserInvitationTokenExample;
import com.example.p2p.entity.Users;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.admin.UserSearchForm;
import com.example.p2p.form.admin.UserUpsertForm;
import com.example.p2p.mapper.ApprovalStepApproverMapper;
import com.example.p2p.mapper.ApprovalTaskMapper;
import com.example.p2p.mapper.PurchaseRequestDetailMapper;
import com.example.p2p.mapper.PurchaseRequestMapper;
import com.example.p2p.mapper.UserInvitationTokenMapper;
import com.example.p2p.mapper.UsersMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

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
    class SearchItems {

        @Nested
        class SearchUsers {

            @BeforeEach
            void setup() {
                jdbcTemplate.update("delete from purchase_order_line");
                jdbcTemplate.update("delete from purchase_request_purchase_order");
                jdbcTemplate.update("delete from purchase_order");
                purchaseRequestDetailMapper.deleteByExample(new PurchaseRequestDetailExample());
                purchaseRequestMapper.deleteByExample(new PurchaseRequestExample());
                approvalStepApproverMapper.deleteByExample(new ApprovalStepApproverExample());
                approvalTaskMapper.deleteByExample(new ApprovalTaskExample());
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
                form.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
            }

            @Test
            void create_duplicate() {
                form.setEmail("sato.hanako@example.com");

                assertThatThrownBy(() -> userService.create(form)).isInstanceOf(BusinessException.class);
            }

            @Test
            void create_success() {
                String userId = userService.create(form);
                assertThat(userId).isNotBlank();

                UsersExample ex = new UsersExample();
                ex.createCriteria().andEmailEqualTo("takagi@example.com");
                Users created = usersMapper.selectByExample(ex).get(0);

                assertThat(created.getUserId()).isNotBlank();
                assertThat(created.getLastName()).isEqualTo("高木");
                assertThat(created.getFirstName()).isEqualTo("太郎");
                assertThat(created.getLastNameKana()).isEqualTo("タカギ");
                assertThat(created.getFirstNameKana()).isEqualTo("タロウ");
                assertThat(created.getEmail()).isEqualTo("takagi@example.com");
                assertThat(created.getPasswordHash()).isNull();
                assertThat(created.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
                assertThat(created.getIsActive()).isFalse();
                assertThat(created.getCreatedAt()).isNotNull();
                assertThat(created.getUpdatedAt()).isNotNull();
                assertThat(created.getIsActive()).isFalse();
            }

        }

    }

    @Nested
    class Update {

        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";

        @Test
        void update_duplicateEmail() {
            UserUpsertForm form = baseForm();
            form.setEmail("siotan0926@gmail.com");

            assertThatThrownBy(() -> userService.update(userId, form)).isInstanceOf(BusinessException.class);
        }

        @Test
        void update_sameEmail() {
            UserUpsertForm form = baseForm();
            form.setIsActive(false);

            assertDoesNotThrow(() -> userService.update(userId, form));
        }

        @Test
        void update_allFieldsChanged() {
            UserUpsertForm form = baseForm();
            form.setLastName("前田");
            form.setFirstName("健太");
            form.setLastNameKana("マエダ");
            form.setFirstNameKana("ケンタ");
            form.setEmail("maeken@example.com");
            form.setRoleId("71dc166d-5059-4fbc-8bca-71d0c5dc2526");
            form.setIsActive(false);

            userService.update(userId, form);

            Users updated = usersMapper.selectByPrimaryKey(userId);
            assertThat(updated.getLastName()).isEqualTo("前田");
            assertThat(updated.getFirstName()).isEqualTo("健太");
            assertThat(updated.getLastNameKana()).isEqualTo("マエダ");
            assertThat(updated.getFirstNameKana()).isEqualTo("ケンタ");
            assertThat(updated.getEmail()).isEqualTo("maeken@example.com");
            assertThat(updated.getPasswordHash())
                .isEqualTo("$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu");
            assertThat(updated.getRoleId()).isEqualTo("71dc166d-5059-4fbc-8bca-71d0c5dc2526");
            assertThat(updated.getIsActive()).isFalse();

        }

        UserUpsertForm baseForm() {
            UserUpsertForm form = new UserUpsertForm();
            form.setLastName("佐藤");
            form.setFirstName("花子");
            form.setLastNameKana("サトウ");
            form.setFirstNameKana("ハナコ");
            form.setEmail("sato.hanako@example.com");
            form.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
            form.setIsActive(true);

            return form;
        }

    }

    @Test
    void create() {
        UserUpsertForm form = new UserUpsertForm();
        form.setLastName("佐藤2");
        form.setFirstName("花子2");
        form.setLastNameKana("サトウ2");
        form.setFirstNameKana("ハナコ2");
        form.setEmail("sato.hanako2@example.com");
        form.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");

       String email = userService.create(form);

        UsersExample ex = new UsersExample();
        ex.createCriteria().andEmailEqualTo("sato.hanako2@example.com");
        Users created = usersMapper.selectByExample(ex).get(0);

        assertThat(created.getUserId()).isNotBlank();
        assertThat(created.getLastName()).isEqualTo("佐藤2");
        assertThat(created.getFirstName()).isEqualTo("花子2");
        assertThat(created.getLastNameKana()).isEqualTo("サトウ2");
        assertThat(created.getFirstNameKana()).isEqualTo("ハナコ2");
        assertThat(created.getEmail()).isEqualTo("sato.hanako2@example.com");
        assertThat(created.getIsActive()).isFalse();
        assertThat(created.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
        assertThat(created.getIsActive()).isFalse();
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isNotNull();
    }

    @Nested
    class Invite {

        @Autowired
        UserInvitationTokenMapper userInvitationTokenMapper;

        @MockitoSpyBean
        JavaMailSender mailSender;

        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";

        @BeforeEach
        void setup() {
            doNothing().when(mailSender).send(any(MimeMessage.class));
            userInvitationTokenMapper.deleteByExample(new UserInvitationTokenExample());
        }

        @Test
        void invite_success() throws MessagingException, IOException {
            MimeMessage message = mailSender.createMimeMessage();
            doReturn(message).when(mailSender).createMimeMessage();
            LocalDateTime fixed = LocalDateTime.of(2026, 4, 16, 10, 10, 5);
            try (MockedConstruction<SecureRandom> mocked = mockConstruction(SecureRandom.class, (mock, ctx) -> {
                doAnswer(inv -> {
                    byte[] bytes = inv.getArgument(0, byte[].class);
                    Arrays.fill(bytes, (byte) 1);
                    return null;
                }).when(mock).nextBytes(any());
            });
                    MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                            Mockito.CALLS_REAL_METHODS)) {
                mockedStatic.when(() -> LocalDateTime.now()).thenReturn(fixed);

                userService.invite(userId);
            }

            UserInvitationToken actual = userInvitationTokenMapper.selectByPrimaryKey(userId);
            assertThat(actual.getTokenHash())
                .isEqualTo("56d5fa7333f6d747db42c239407e5da4c32f4c79f35d092b134fd35a402d9c5c");
            assertThat(actual.getExpiresAt()).isEqualTo(LocalDateTime.of(2026, 4, 23, 10, 10, 5));
            assertThat(actual.getCreatedAt()).isNotNull();

            assertThat(message.getHeader("From", null)).contains("temp@example.com");
            assertThat(message.getHeader("To", null)).contains("sato.hanako@example.com");
            assertThat(message.getSubject()).isEqualTo("パスワード初回設定");

            assertThat((String) message.getContent()).contains("初期パスワード設定", "<a href=\"http://local", "setup?token=AQE");

        }

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        void invite_error() {
            try (MockedConstruction<MimeMessageHelper> mocked = mockConstruction(MimeMessageHelper.class,
                    (mock, ctx) -> doThrow(MessagingException.class).when(mock).setFrom(anyString()))) {
                assertThrows(RuntimeException.class, () -> userService.invite(userId));
            }

            assertThat(userInvitationTokenMapper.selectByPrimaryKey(userId)).isNull();
        }

        @Test
        void invite_resend() {
            UserInvitationToken token = new UserInvitationToken();
            token.setUserId(userId);
            token.setTokenHash("a".repeat(64));
            token.setExpiresAt(LocalDateTime.now());
            userInvitationTokenMapper.insertSelective(token);

            userService.invite(userId);

            UserInvitationToken actual = userInvitationTokenMapper.selectByPrimaryKey(userId);
            assertThat(actual.getTokenHash()).isNotEqualTo("a".repeat(64));

            verify(mailSender).send(any(MimeMessage.class));
        }

    }

    @ParameterizedTest
    @CsvSource(value = {"testPass123, true, false", "null, false, true"}, nullValues = "null")
    void getUserDetail_canInvite(String password, boolean isActive, boolean expected) {
        jdbcTemplate.update("delete from purchase_order_line");
        jdbcTemplate.update("delete from purchase_request_purchase_order");
        jdbcTemplate.update("delete from purchase_order");
        purchaseRequestDetailMapper.deleteByExample(new PurchaseRequestDetailExample());
        purchaseRequestMapper.deleteByExample(new PurchaseRequestExample());
        approvalStepApproverMapper.deleteByExample(new ApprovalStepApproverExample());
        approvalTaskMapper.deleteByExample(new ApprovalTaskExample());
        usersMapper.deleteByExample(new UsersExample());
        Users u = new Users();
        u.setLastName("a");
        u.setFirstName("a");
        u.setLastNameKana("ア");
        u.setFirstNameKana("イ");
        u.setEmail("example@example.com");
        u.setPasswordHash(password);
        u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
        u.setIsActive(isActive);
        usersMapper.insertSelective(u);
        
        UsersExample ex = new UsersExample();
        ex.createCriteria().andEmailEqualTo("example@example.com");
        Users inserted = usersMapper.selectByExample(ex).get(0);
        
       UserDetailDto actual = userService.getUserDetail(inserted.getUserId());
       assertThat(actual.isCanInvite()).isEqualTo(expected);
    }
}
