package com.example.p2p.controller.app;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.dto.app.UserProfileDto;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.app.EmailChangeForm;
import com.example.p2p.form.app.InitialPasswordSetupForm;
import com.example.p2p.form.app.ProfileEditForm;
import com.example.p2p.security.CustomUserDetails;
import com.example.p2p.service.app.AccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {

    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private AccountService accountService;

    private MessageSource messageSource;

    // TODO: アカウントホーム画面
    @GetMapping
    public String getMethodName() {
        return "/app/test";
    }

    /* パスワード初回設定 */
    @GetMapping("/initial-password-setup")
    public String showInitialPasswordSetupForm(@RequestParam String token,
            @ModelAttribute("form") InitialPasswordSetupForm form, Model model) {
        logger.debug("初回パスワード設定画面表示開始");

        model.addAttribute("user", accountService.getInvitedUserInfo(token));
        form.setToken(token);

        logger.debug("初回パスワード設定画面表示完了");
        return "app/initial-password-setup";
    }

    @PostMapping("/initial-password-setup")
    public String accept(@Valid @ModelAttribute("form") InitialPasswordSetupForm form, BindingResult result,
            Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) throws ServletException {
        logger.info("初回パスワード設定開始");
        if (result.hasErrors()) {
            logger.warn("初回パスワード設定バリデーションエラー");
            model.addAttribute("user", accountService.getInvitedUserInfo(form.getToken()));
            return "app/initial-password-setup";
        }
        String email;
        try {
            email = accountService.acceptInvitation(form);
        }
        catch (BusinessException e) {
            logger.warn("初回パスワード設定業務エラー");
            model.addAttribute("user", accountService.getInvitedUserInfo(form.getToken()));
            result.reject("account.invitation.invalid");
            return "app/initial-password-setup";
        }
        // 自動ログイン
        request.login(email, form.getPassword());

        logger.info("初回パスワード設定成功");
        // TODO: 仮値
        return "redirect:/test";
    }

    /* プロフィール編集 */
    @GetMapping("/profile")
    public String showProfileEditForm(@AuthenticationPrincipal(expression = "username") String userId,
            @ModelAttribute("form") ProfileEditForm form) {
        logger.debug("プロフィール編集画面表示開始");
        UserProfileDto dto = accountService.getUserProfile(userId);
        modelMapper.map(dto, form);

        logger.debug("プロフィール編集画面表示完了");
        return "app/profile-edit";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal(expression = "username") String userId,
            @Valid @ModelAttribute("form") ProfileEditForm form, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        logger.info("プロフィール更新開始");

        if (bindingResult.hasErrors()) {
            logger.warn("プロフィール更新バリデーションエラー");

            return "app/profile-edit";
        }
        accountService.updateProfile(userId, form);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.update.success", null, null));

        logger.info("プロフィール更新完了");

        return "redirect:/account/profile";
    }

    /* メールアドレス変更 */
    @GetMapping("/email-change/request")
    public String showEmailChangeForm(@AuthenticationPrincipal(expression = "email") String email,
            @ModelAttribute("form") EmailChangeForm form, Model model) {
        logger.debug("メール変更画面表示開始");

        model.addAttribute("email", email);

        logger.debug("メール変更画面表示完了");
        return "app/email-change-request";
    }

    @PostMapping("/email-change/request")
    public String requestEmailChange(@AuthenticationPrincipal CustomUserDetails principal,
            @Valid @ModelAttribute("form") EmailChangeForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("メール変更申請開始");

        if (bindingResult.hasErrors()) {
            logger.warn("メール変更申請バリデーションエラー");
            return "app/email-change-request";
        }
        try {
            accountService.requestEmailChange(principal.getUsername(), form.getNewEmail());
        }
        catch (BusinessException e) {
            logger.warn("メール変更申請業務エラー");
            model.addAttribute("email", principal.getEmail());
            bindingResult.reject("common.duplicate");
            return "app/email-change-request";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("account.email.change.mail.sent", null, null));

        logger.info("メール変更申請完了");

        return "redirect:/account/email-change/sent";
    }

    @GetMapping("/email-change/sent")
    public String showEmailChangeSent() {
        logger.debug("メールアドレス変更確認メール送信完了画面表示開始");

        return "app/email-change-sent";
    }

    @GetMapping("/email-change/confirm")
    public String confirmEmailChange(@RequestParam @NotBlank String token, Model model) {
        logger.info("メール変更確定開始");

        try {
            accountService.confirmEmailChange(token);
        }
        catch (BusinessException e) {
            logger.warn("メール変更確定業務エラー");
            model.addAttribute("errorMessage",
                    messageSource.getMessage("account.email.change.confirm.invalid", null, null));
            return "app/email-change-invalid";
        }

        logger.info("メール変更確定完了");

        return "/app/email-change-complete";
    }

}
