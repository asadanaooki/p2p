package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
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
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.EmailChangeRequest;
import com.example.p2p.entity.EmailChangeRequestExample;
import com.example.p2p.entity.UserInvitationToken;
import com.example.p2p.entity.UserInvitationTokenExample;
import com.example.p2p.entity.Users;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.app.InitialPasswordSetupForm;
import com.example.p2p.form.app.ProfileEditForm;
import com.example.p2p.mapper.EmailChangeRequestMapper;
import com.example.p2p.mapper.UserInvitationTokenMapper;
import com.example.p2p.mapper.UsersMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @MockitoSpyBean
    UserInvitationTokenMapper userInvitationTokenMapper;

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailChangeRequestMapper emailChangeRequestMapper;

    @Nested
    class AcceptInvitation {

        InitialPasswordSetupForm form;

        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";

        @BeforeEach
        void setup() {
            userInvitationTokenMapper.deleteByExample(new UserInvitationTokenExample());
            UserInvitationToken token = new UserInvitationToken();
            token.setUserId(userId);
            token.setTokenHash("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
            token.setExpiresAt(LocalDateTime.of(2026, 1, 1, 0, 0));
            userInvitationTokenMapper.insertSelective(token);

            form = new InitialPasswordSetupForm();
            form.setToken("abc");
            form.setPassword("testPass");
            form.setConfirmPassword("testPass");
        }

        @Test
        void acceptInvitation_success() {
            LocalDateTime fixed = LocalDateTime.of(2025, 4, 18, 0, 0);
            try (MockedStatic<LocalDateTime> mock = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
                mock.when(() -> LocalDateTime.now()).thenReturn(fixed);
                String email = accountService.acceptInvitation(form);

                assertThat(email).isEqualTo("sato.hanako@example.com");
            }
            Users updated = usersMapper.selectByPrimaryKey(userId);
            assertThat(passwordEncoder.matches("testPass", updated.getPasswordHash())).isTrue();
            assertThat(updated.getIsActive()).isTrue();
            assertThat(userInvitationTokenMapper.selectByPrimaryKey(userId)).isNull();

            assertThat(updated.getLastName()).isEqualTo("佐藤");
            assertThat(updated.getFirstName()).isEqualTo("花子");
            assertThat(updated.getLastNameKana()).isEqualTo("サトウ");
            assertThat(updated.getFirstNameKana()).isEqualTo("ハナコ");
            assertThat(updated.getEmail()).isEqualTo("sato.hanako@example.com");
            assertThat(updated.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
            assertThat(updated.getCreatedAt().truncatedTo(ChronoUnit.MILLIS))
                .isEqualTo(LocalDateTime.of(2026, 4, 20, 12, 22, 39, 902_000_000));
            assertThat(updated.getUpdatedAt()).isAfter(LocalDateTime.of(2026, 4, 18, 15, 3, 39, 32_000_000));

        }

        @Test
        void acceptInvitation_notFound() {
            assertThatThrownBy(() -> accountService.acceptInvitation(form)).isInstanceOf(BusinessException.class);
        }

        @Test
        void acceptInvitation_expired() {
            LocalDateTime fixed = LocalDateTime.of(2026, 4, 18, 0, 0);
            try (MockedStatic<LocalDateTime> mock = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
                mock.when(() -> LocalDateTime.now()).thenReturn(fixed);
                assertThatThrownBy(() -> accountService.acceptInvitation(form)).isInstanceOf(BusinessException.class);
            }
        }

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        void acceptInvitation_rollback() {
            LocalDateTime fixed = LocalDateTime.of(2025, 4, 18, 0, 0);
            try (MockedStatic<LocalDateTime> mock = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
                mock.when(() -> LocalDateTime.now()).thenReturn(fixed);
                doThrow(new IllegalArgumentException()).when(userInvitationTokenMapper).deleteByPrimaryKey(anyString());
                assertThrows(IllegalArgumentException.class, () -> accountService.acceptInvitation(form));
            }
            Users before = usersMapper.selectByPrimaryKey(userId);
            assertThat(before.getPasswordHash())
                .isEqualTo("$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu");
            assertThat(before.getIsActive()).isTrue();

            userInvitationTokenMapper.deleteByExample(new UserInvitationTokenExample());
        }

    }

    @Test
    void updateProfile() {
        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";
        ProfileEditForm form = new ProfileEditForm();
        form.setLastName("森田");
        form.setFirstName("雄大");
        form.setLastNameKana("モリタ");
        form.setFirstNameKana("ユウダイ");

        accountService.updateProfile(userId, form);

        Users updated = usersMapper.selectByPrimaryKey(userId);

        assertThat(updated.getLastName()).isEqualTo("森田");
        assertThat(updated.getFirstName()).isEqualTo("雄大");
        assertThat(updated.getLastNameKana()).isEqualTo("モリタ");
        assertThat(updated.getFirstNameKana()).isEqualTo("ユウダイ");
        assertThat(updated.getEmail()).isEqualTo("sato.hanako@example.com");
        assertThat(updated.getPasswordHash()).isEqualTo("$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu");
        assertThat(updated.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
        assertThat(updated.getIsActive()).isTrue();
    }

    @Nested
    class RequestEmailChange {

        @MockitoSpyBean
        JavaMailSender mailSender;

        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";

        @BeforeEach
        void setup() {
            doNothing().when(mailSender).send(any(MimeMessage.class));
            emailChangeRequestMapper.deleteByExample(new EmailChangeRequestExample());
        }

        @Test
        void requestEmailChange_success() throws MessagingException, IOException {
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

                accountService.requestEmailChange(userId, "test@example.com");
            }

            EmailChangeRequest actual = emailChangeRequestMapper.selectByPrimaryKey(userId);
            assertThat(actual.getTokenHash())
                .isEqualTo("56d5fa7333f6d747db42c239407e5da4c32f4c79f35d092b134fd35a402d9c5c");
            assertThat(actual.getNewEmail()).isEqualTo("test@example.com");
            assertThat(actual.getExpiresAt()).isEqualTo(LocalDateTime.of(2026, 4, 17, 10, 10, 5));
            assertThat(actual.getCreatedAt()).isNotNull();

            assertThat(message.getHeader("From", null)).contains("temp@example.com");
            assertThat(message.getHeader("To", null)).contains("test@example.com");
            assertThat(message.getSubject()).isEqualTo("メールアドレス変更の確認");

            assertThat((String) message.getContent()).contains("メールアドレス変更のお申し込み", "変更確認", "<a href=\"http://local",
                    "email-change/confirm?token=AQE");

        }

        @Test
        void requestEmailChange_duplicate() {
            assertThatThrownBy(() -> accountService.requestEmailChange(userId, "siotan0926@gmail.com"))
                .isInstanceOf(BusinessException.class);
        }

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        void requestEmailChange_error() {
            try (MockedConstruction<MimeMessageHelper> mocked = mockConstruction(MimeMessageHelper.class,
                    (mock, ctx) -> doThrow(MessagingException.class).when(mock).setFrom(anyString()))) {
                assertThrows(RuntimeException.class,
                        () -> accountService.requestEmailChange(userId, "test@example.com"));
            }

            assertThat(emailChangeRequestMapper.selectByPrimaryKey(userId)).isNull();
        }

        @Test
        void requestEmailChange_resend() {
            EmailChangeRequest request = new EmailChangeRequest();
            request.setUserId(userId);
            request.setNewEmail("test@example.com");
            request.setTokenHash("a".repeat(64));
            request.setExpiresAt(LocalDateTime.now());
            emailChangeRequestMapper.insertSelective(request);

            accountService.requestEmailChange(userId, "test@example.com");

            EmailChangeRequest actual = emailChangeRequestMapper.selectByPrimaryKey(userId);
            assertThat(actual.getNewEmail()).isEqualTo("test@example.com");
            assertThat(actual.getTokenHash()).isNotEqualTo("a".repeat(64));

            verify(mailSender).send(any(MimeMessage.class));
        }

    }

    @Nested
    class ConfirmEmailChange {

        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";

        String token = "testtoken";

        @BeforeEach
        void setup() {
            EmailChangeRequest request = new EmailChangeRequest();
            request.setUserId(userId);
            // testtoken
            request.setTokenHash("ada63e98fe50eccb55036d88eda4b2c3709f53c2b65bc0335797067e9a2a5d8b");
            request.setNewEmail("sample123@example.com");
            request.setExpiresAt(LocalDateTime.of(2300, 1, 1, 0, 0));
            emailChangeRequestMapper.insertSelective(request);
        }

        @Test
        void confirmEmailChange_notFound() {
            emailChangeRequestMapper.deleteByPrimaryKey(userId);

            assertThatThrownBy(() -> accountService.confirmEmailChange(token)).isInstanceOf(BusinessException.class);
        }

        @Test
        void confirmEmailChange_expired() {
            EmailChangeRequest request = new EmailChangeRequest();
            request.setUserId(userId);
            request.setExpiresAt(LocalDateTime.now().minusHours(3));
            emailChangeRequestMapper.updateByPrimaryKeySelective(request);

            assertThatThrownBy(() -> accountService.confirmEmailChange(token)).isInstanceOf(BusinessException.class);
        }

        @Test
        void confirmEmailChange_success() {
            accountService.confirmEmailChange(token);

            assertThat(emailChangeRequestMapper.selectByPrimaryKey(userId)).isNull();

            Users user = usersMapper.selectByPrimaryKey(userId);
            assertThat(user.getLastName()).isEqualTo("佐藤");
            assertThat(user.getFirstName()).isEqualTo("花子");
            assertThat(user.getLastNameKana()).isEqualTo("サトウ");
            assertThat(user.getFirstNameKana()).isEqualTo("ハナコ");
            assertThat(user.getEmail()).isEqualTo("sample123@example.com");
            assertThat(user.getPasswordHash())
                .isEqualTo("$2a$08$RtfQTBKqoBSHYRXwmuV7GuTnQPaLq24x0elYL5kIStLEWOSjaQcsu");
            assertThat(user.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
            assertThat(user.getIsActive()).isTrue();
        }

    }

}
