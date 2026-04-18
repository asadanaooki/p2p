package com.example.p2p.controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.p2p.form.InitialPasswordSetupForm;
import com.example.p2p.service.app.AccountService;

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

}
