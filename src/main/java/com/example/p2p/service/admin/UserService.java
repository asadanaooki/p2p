package com.example.p2p.service.admin;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.RoleOptionDto;
import com.example.p2p.dto.UserDetailDto;
import com.example.p2p.dto.UserListRowDto;
import com.example.p2p.dto.UserListViewDto;
import com.example.p2p.entity.UserInvitationToken;
import com.example.p2p.entity.Users;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.form.UserUpsertForm;
import com.example.p2p.mapper.RoleMapperCustom;
import com.example.p2p.mapper.UserInvitationTokenMapper;
import com.example.p2p.mapper.UsersMapper;
import com.example.p2p.mapper.UsersMapperCustom;
import com.example.p2p.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private UsersMapper usersMapper;

    private UsersMapperCustom usersMapperCustom;

    private RoleMapperCustom roleMapperCustom;

    private ModelMapper modelMapper;

    private JavaMailSender mailSender;

    private UserInvitationTokenMapper userInvitationTokenMapper;

    public UserListViewDto searchUsers(UserSearchForm form) {
        UserListViewDto dto = new UserListViewDto();
        int page = form.getPage();
        List<UserListRowDto> users = usersMapperCustom.selectUsers(form);
        dto.setUsers(users);
        dto.setRoleOptions(roleMapperCustom.selectRoleOptions());
        dto.setCurrentPage(page);
        dto.setPageNumberList(
                CommonUtil.createPageNumbers(usersMapperCustom.countUsers(form), form.getSize(), page, 2));
        return dto;
    }

    public UserDetailDto getUserDetail(String userId) {
        return usersMapperCustom.selectUserDetail(userId);
    }

    public String create(UserUpsertForm form) {
        UsersExample ex = new UsersExample();
        ex.createCriteria().andEmailEqualTo(form.getEmail());
        // 重複チェック
        if (usersMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        Users u = modelMapper.map(form, Users.class);
        usersMapper.insertSelective(u);
        return u.getUserId();
    }

    public void update(String userId, UserUpsertForm form) {
        UsersExample ex = new UsersExample();
        ex.createCriteria().andEmailEqualTo(form.getEmail()).andUserIdNotEqualTo(userId);
        // 重複チェック
        if (usersMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        Users u = modelMapper.map(form, Users.class);
        u.setUserId(userId);
        usersMapper.updateByPrimaryKeySelective(u);
    }

    @Transactional
    public void invite(String userId) {
        Users user = usersMapper.selectByPrimaryKey(userId);
        userInvitationTokenMapper.deleteByPrimaryKey(userId);
        String token = generateToken();
        saveInvitationToken(userId, token);
        sendMail(user.getEmail(), token);
    }

    public List<RoleOptionDto> getRoleOptions() {
        return roleMapperCustom.selectRoleOptions();
    }

    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return token;
    }

    private void saveInvitationToken(String userId, String token) {
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        HexFormat hex = HexFormat.of();
        String tokenHash = hex.formatHex(sha256.digest(token.getBytes(StandardCharsets.UTF_8)));
        UserInvitationToken uit = new UserInvitationToken();
        uit.setUserId(userId);
        uit.setTokenHash(tokenHash);
        uit.setExpiresAt(LocalDateTime.now().plusDays(7));

        userInvitationTokenMapper.insertSelective(uit);
    }

    private void sendMail(String email, String token) {
        MimeMessage message = mailSender.createMimeMessage();

        // TODO: 仮値
        String url = "http://localhost:8080/account/initial-password-setup?token=" + token;
        String body = """
                <p>初期パスワード設定のご案内です。</p>
                <p><a href="%s">こちら</a>をクリックしてください。</p>
                """.formatted(url);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            // TODO: 仮値
            helper.setFrom("temp@example.com");
            helper.setTo(email);
            helper.setText(body, true);
            helper.setSubject("パスワード初回設定");

            mailSender.send(message);
        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
