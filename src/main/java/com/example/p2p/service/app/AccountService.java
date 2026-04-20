package com.example.p2p.service.app;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.app.InitialPasswordSetupViewDto;
import com.example.p2p.dto.app.UserProfileDto;
import com.example.p2p.entity.UserInvitationToken;
import com.example.p2p.entity.UserInvitationTokenExample;
import com.example.p2p.entity.Users;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.app.InitialPasswordSetupForm;
import com.example.p2p.form.app.ProfileEditForm;
import com.example.p2p.mapper.UserInvitationTokenMapper;
import com.example.p2p.mapper.UserInvitationTokenMapperCustom;
import com.example.p2p.mapper.UsersMapper;
import com.example.p2p.mapper.UsersMapperCustom;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private UsersMapper usersMapper;
    
    private UsersMapperCustom usersMapperCustom;

    private UserInvitationTokenMapper userInvitationTokenMapper;

    private UserInvitationTokenMapperCustom userInvitationTokenMapperCustom;

    private PasswordEncoder passwordEncoder;

    private ModelMapper modelMapper;

    @Transactional
    public String acceptInvitation(InitialPasswordSetupForm form) {
        logger.info("招待受諾開始");
        // トークンをハッシュ化
        String tokenHash = CommonUtil.hashToken(form.getToken());

        // ハッシュ化トークンに一致するデータを取得
        UserInvitationTokenExample ex = new UserInvitationTokenExample();
        ex.createCriteria().andTokenHashEqualTo(tokenHash);
        List<UserInvitationToken> invitationTokens = userInvitationTokenMapper.selectByExample(ex);
        if (invitationTokens.isEmpty()) {
            logger.warn("該当トークンなし");
            throw new BusinessException();
        }

        // 有効期限チェック
        UserInvitationToken invitationToken = invitationTokens.get(0);
        if (LocalDateTime.now().isAfter(invitationToken.getExpiresAt())) {
            logger.warn("有効期限切れ");
            throw new BusinessException();
        }

        // 紐づくユーザーのPWとステータスを更新
        Users user = usersMapper.selectByPrimaryKey(invitationToken.getUserId());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setIsActive(true);
        usersMapper.updateByPrimaryKey(user);

        // トークン削除
        userInvitationTokenMapper.deleteByPrimaryKey(user.getUserId());

        logger.info("招待受諾完了");
        return user.getEmail();
    }

    public InitialPasswordSetupViewDto getInvitedUserInfo(String token) {
        logger.debug("招待ユーザー情報取得開始");
        String tokenHash = CommonUtil.hashToken(token);
        return userInvitationTokenMapperCustom.selectUserNameAndEmail(tokenHash);
    }
    
    public UserProfileDto getUserProfile(String userId) {
        logger.debug("プロフィール情報取得開始");
        return usersMapperCustom.selectUserProfile(userId);
    }

    public void updateProfile(String userId, ProfileEditForm form) {
        logger.info("プロフィール編集開始");
        Users user = modelMapper.map(form, Users.class);
        user.setUserId(userId);
        usersMapper.updateByPrimaryKeySelective(user);
        logger.info("プロフィール編集完了");
    }

}
