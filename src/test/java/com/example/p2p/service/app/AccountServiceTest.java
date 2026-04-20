package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.UserInvitationToken;
import com.example.p2p.entity.UserInvitationTokenExample;
import com.example.p2p.entity.Users;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.InitialPasswordSetupForm;
import com.example.p2p.form.ProfileEditForm;
import com.example.p2p.mapper.UserInvitationTokenMapper;
import com.example.p2p.mapper.UsersMapper;

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
            assertThat(updated.getUpdatedAt())
                    .isAfter(LocalDateTime.of(2026, 4, 18, 15, 3, 39, 32_000_000));

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
}
