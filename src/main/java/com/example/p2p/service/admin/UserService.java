package com.example.p2p.service.admin;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserListViewDto searchUsers(UserSearchForm form) {
        logger.debug("ユーザー検索開始");

        UserListViewDto dto = new UserListViewDto();
        int page = form.getPage();
        List<UserListRowDto> users = usersMapperCustom.selectUsers(form);

        dto.setUsers(users);
        dto.setRoleOptions(roleMapperCustom.selectRoleOptions());
        dto.setCurrentPage(page);
        dto.setPageNumberList(
                CommonUtil.createPageNumbers(usersMapperCustom.countUsers(form), form.getSize(), page, 2));

        logger.debug("ユーザー検索完了");
        return dto;
    }

    public UserDetailDto getUserDetail(String userId) {
        logger.debug("ユーザー詳細取得開始");
        
       var a = usersMapper.selectByExample(new UsersExample());

        UserDetailDto userDetail = usersMapperCustom.selectUserDetail(userId);
        UsersExample ex = new UsersExample();
        ex.createCriteria().andUserIdEqualTo(userId).andPasswordHashIsNull().andIsActiveEqualTo(false);
        userDetail.setCanInvite(usersMapper.countByExample(ex) > 0);

        logger.debug("ユーザー詳細取得完了");
        return userDetail;
    }

    public String create(UserUpsertForm form) {
        logger.info("ユーザー作成処理開始");

        UsersExample ex = new UsersExample();
        ex.createCriteria().andEmailEqualTo(form.getEmail());

        if (usersMapper.countByExample(ex) > 0) {
            logger.warn("ユーザー作成時の重複エラー");
            throw new BusinessException();
        }

        Users u = modelMapper.map(form, Users.class);
        usersMapper.insertSelective(u);

        logger.info("ユーザー作成処理成功");
        return u.getUserId();
    }

    public void update(String userId, UserUpsertForm form) {
        logger.info("ユーザー更新処理開始");

        UsersExample ex = new UsersExample();
        ex.createCriteria().andEmailEqualTo(form.getEmail()).andUserIdNotEqualTo(userId);

        if (usersMapper.countByExample(ex) > 0) {
            logger.warn("ユーザー更新時の重複エラー");
            throw new BusinessException();
        }

        Users u = modelMapper.map(form, Users.class);
        u.setUserId(userId);
        usersMapper.updateByPrimaryKeySelective(u);

        logger.info("ユーザー更新処理成功");
    }

    @Transactional
    public void invite(String userId) {
        logger.info("ユーザー招待開始");

        Users user = usersMapper.selectByPrimaryKey(userId);
        userInvitationTokenMapper.deleteByPrimaryKey(userId);
        String token = generateToken();
        saveInvitationToken(userId, token);
        sendMail(user.getEmail(), token);

        logger.info("ユーザー招待成功");
    }

    public List<RoleOptionDto> getRoleOptions() {
        logger.debug("ロール選択肢取得開始");

        List<RoleOptionDto> roleOptions = roleMapperCustom.selectRoleOptions();

        logger.debug("ロール選択肢取得完了");
        return roleOptions;
    }

    private String generateToken() {
        logger.debug("招待トークン生成開始");

        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        logger.debug("招待トークン生成完了");
        return token;
    }

    private void saveInvitationToken(String userId, String token) {
        logger.debug("招待トークン保存開始");
        String tokenHash = CommonUtil.hashToken(token);

        UserInvitationToken uit = new UserInvitationToken();
        uit.setUserId(userId);
        uit.setTokenHash(tokenHash);
        uit.setExpiresAt(LocalDateTime.now().plusDays(7));

        userInvitationTokenMapper.insertSelective(uit);

        logger.debug("招待トークン保存完了");
    }

    private void sendMail(String email, String token) {
        logger.info("招待メール送信開始");

        MimeMessage message = mailSender.createMimeMessage();

        String url = "http://localhost:8080/account/initial-password-setup?token=" + token;
        String body = """
                <p>初期パスワード設定のご案内です。</p>
                <p><a href="%s">こちら</a>をクリックしてください。</p>
                """.formatted(url);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom("temp@example.com");
            helper.setTo(email);
            helper.setText(body, true);
            helper.setSubject("パスワード初回設定");

            mailSender.send(message);

            logger.info("招待メール送信成功");
        }
        catch (MessagingException e) {
            logger.error("招待メール送信失敗", e);
            throw new RuntimeException(e);
        }
    }

}
