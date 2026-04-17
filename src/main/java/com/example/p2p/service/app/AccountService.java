package com.example.p2p.service.app;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.UserInvitationToken;
import com.example.p2p.entity.UserInvitationTokenExample;
import com.example.p2p.entity.Users;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.InitialPasswordSetupForm;
import com.example.p2p.mapper.UserInvitationTokenMapper;
import com.example.p2p.mapper.UsersMapper;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private UsersMapper usersMapper;

    private UserInvitationTokenMapper userInvitationTokenMapper;

    private PasswordEncoder passwordEncoder;

    @Transactional
    public void acceptInvitation(InitialPasswordSetupForm form) {
        logger.info("招待受諾開始");
        // トークンをハッシュ化
        String tokenHash = CommonUtil.hashToken(form.getToken());

        // ハッシュ化トークンで該当のデータ取得
        // 見つからない場合は業務エラー
        UserInvitationTokenExample ex = new UserInvitationTokenExample();
        ex.createCriteria().andTokenHashEqualTo(tokenHash);
        List<UserInvitationToken> invitationTokens = userInvitationTokenMapper.selectByExample(ex);
        if (invitationTokens.isEmpty()) {
            logger.warn("該当トークンなし");
            throw new BusinessException();
        }
        
        // 有効期限チェック
        // 期限切れの場合は業務エラー
        UserInvitationToken invitationToken = invitationTokens.get(0);
        if (LocalDateTime.now().isAfter(invitationToken.getExpiresAt())) {
            logger.warn("有効期限切れ");
            throw new BusinessException();
        }

        // 紐づくユーザーIDから、ユーザーのPWとステータスを更新
        String userId = invitationToken.getUserId();
        Users user = new Users();
        user.setUserId(userId);
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setIsActive(true);
        usersMapper.updateByPrimaryKeySelective(user);

        // トークン削除
        userInvitationTokenMapper.deleteByPrimaryKey(userId);

        logger.info("招待受諾完了");
    }

}
