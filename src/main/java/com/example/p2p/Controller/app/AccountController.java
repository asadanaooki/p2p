package com.example.p2p.controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.InitialPasswordSetupForm;
import com.example.p2p.service.app.AccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private AccountService accountService;

    private MessageSource messageSource;

    @GetMapping("/initial-password-setup")
    public String showInitialPasswordSetupForm(@RequestParam String token,
            @ModelAttribute("form") InitialPasswordSetupForm form, Model model) {
        logger.debug("初回パスワード設定画面表示開始");

        model.addAttribute("user", accountService.getInvitedUserInfo(token));
        form.setToken(token);

        logger.debug("初回パスワード設定画面表示完了");
        return "initial-password-setup";
    }

    @PostMapping("/initial-password-setup")
    public String accept(@Valid @ModelAttribute("form") InitialPasswordSetupForm form, BindingResult result,
            Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) throws ServletException {
        if (result.hasErrors()) {
            model.addAttribute("user", accountService.getInvitedUserInfo(form.getToken()));
            return "initial-password-setup";
        }
        String email;
        try {
            email = accountService.acceptInvitation(form);
        }
        catch (BusinessException e) {
            model.addAttribute("user", accountService.getInvitedUserInfo(form.getToken()));
            result.reject("account.invitation.invalid");
            return "initial-password-setup";
        }
        // 自動ログイン
        request.login(email, form.getPassword());
        
        // TODO: 仮値
        return "redirect:/test";
    }

}
