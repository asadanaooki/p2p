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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.p2p.dto.app.PurchaseRequestDetailDto;
import com.example.p2p.dto.app.UserProfileDto;
import com.example.p2p.form.app.ProfileEditForm;
import com.example.p2p.form.app.PurchaseRequestSearchForm;
import com.example.p2p.service.app.PurchaseRequestService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/purchase-request")
@AllArgsConstructor
public class PurchaseRequestController {

    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(PurchaseRequestController.class);

    private static final String LAST_SEARCH_CONDITION = "lastSearchCondition";

    private PurchaseRequestService purchaseRequestService;

    private MessageSource messageSource;
    
    @GetMapping
    public String showPurchaseRequestList(
            @Valid @ModelAttribute("form") PurchaseRequestSearchForm form,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {
        logger.debug("PR一覧画面表示開始");

        if (bindingResult.hasErrors()) {
            PurchaseRequestSearchForm lastCondition = (PurchaseRequestSearchForm) session
                .getAttribute(LAST_SEARCH_CONDITION);
            PurchaseRequestSearchForm formToSearch = lastCondition == null ? new PurchaseRequestSearchForm()
                    : lastCondition;
            model.addAttribute("view", purchaseRequestService.searchPurchaseRequests(formToSearch));
            return "app/purchase-request-list";
        }
        session.setAttribute(LAST_SEARCH_CONDITION, form);
        model.addAttribute("view", purchaseRequestService.searchPurchaseRequests(form));

        logger.debug("PR一覧画面表示完了");
        return "app/purchase-request-list";
    }
    
    @GetMapping("/{prId}")
    public String showPurchaseRequestDetail(@PathVariable @NotBlank String prId,
            Model model) {
        logger.debug("PR詳細画面表示開始");
        
        model.addAttribute("view", purchaseRequestService.getPurchaseRequestDetail(prId));

        logger.debug("PR詳細画面表示完了");
        return "app/purchase-request-detail";
    }

    // @PostMapping("/initial-password-setup")
    // public String accept(@Valid @ModelAttribute("form") InitialPasswordSetupForm form,
    // BindingResult result,
    // Model model, HttpServletRequest request, RedirectAttributes redirectAttributes)
    // throws ServletException {
    // logger.info("初回パスワード設定開始");
    // if (result.hasErrors()) {
    // logger.warn("初回パスワード設定バリデーションエラー");
    // model.addAttribute("user", accountService.getInvitedUserInfo(form.getToken()));
    // return "app/initial-password-setup";
    // }
    // String email;
    // try {
    // email = accountService.acceptInvitation(form);
    // }
    // catch (BusinessException e) {
    // logger.warn("初回パスワード設定業務エラー");
    // model.addAttribute("user", accountService.getInvitedUserInfo(form.getToken()));
    // result.reject("account.invitation.invalid");
    // return "app/initial-password-setup";
    // }
    // // 自動ログイン
    // request.login(email, form.getPassword());
    //
    // logger.info("初回パスワード設定成功");
    // // TODO: 仮値
    // return "redirect:/test";
    // }
    //
    // @GetMapping("/profile")
    // public String showProfileEditForm(@AuthenticationPrincipal(expression = "username")
    // String userId,
    // @ModelAttribute("form") ProfileEditForm form) {
    // logger.debug("プロフィール編集画面表示開始");
    // UserProfileDto dto = accountService.getUserProfile(userId);
    // modelMapper.map(dto, form);
    //
    // logger.debug("プロフィール編集画面表示完了");
    // return "app/profile-edit";
    // }
    //
    // @PostMapping("/profile")
    // public String updateProfile(@AuthenticationPrincipal(expression = "username")
    // String userId,
    // @Valid @ModelAttribute("form") ProfileEditForm form,
    // BindingResult bindingResult,
    // RedirectAttributes redirectAttributes) {
    // logger.info("プロフィール更新開始");
    //
    // if (bindingResult.hasErrors()) {
    // logger.warn("プロフィール更新バリデーションエラー");
    //
    // return "app/profile-edit";
    // }
    // accountService.updateProfile(userId, form);
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("common.update.success", null, null));
    //
    // logger.info("プロフィール更新完了");
    //
    // return "redirect:/account/profile";
    // }

}
