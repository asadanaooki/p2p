package com.example.p2p.controller.app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.form.app.PurchaseRequestCreateForm;
import com.example.p2p.form.app.PurchaseRequestSearchForm;
import com.example.p2p.service.app.PurchaseRequestService;

import jakarta.servlet.ServletException;
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String showPurchaseRequestList(@Valid @ModelAttribute("form") PurchaseRequestSearchForm form,
            BindingResult bindingResult, Model model, HttpSession session) {
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
    public String showPurchaseRequestDetail(@PathVariable @NotBlank String prId, Model model) {
        logger.debug("PR詳細画面表示開始");

        model.addAttribute("view", purchaseRequestService.getPurchaseRequestDetail(prId));

        logger.debug("PR詳細画面表示完了");
        return "app/purchase-request-detail";
    }

    @GetMapping("/create")
    public String showPurchaseRequestCreateForm(@AuthenticationPrincipal(expression = "username") String userId,
            @ModelAttribute("form") PurchaseRequestCreateForm form, Model model) {
        logger.debug("PR作成画面表示開始");

        model.addAttribute("view", purchaseRequestService.prepareCreateView(userId));

        logger.debug("PR作成画面表示完了");

        return "app/purchase-request-create";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal(expression = "username") String userId,
            @Valid @ModelAttribute("form") PurchaseRequestCreateForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) throws ServletException {
        logger.info("PR作成開始");

        if (bindingResult.hasErrors()) {
            logger.warn("PR作成バリデーションエラー");

            model.addAttribute("view", purchaseRequestService.prepareCreateView(userId));
            model.addAttribute("detailErrorMessages", createDetailErrorMessages(bindingResult));
            return "app/purchase-request-create";
        }
        String prId = purchaseRequestService.create(userId, form);

        redirectAttributes.addAttribute("prId", prId);

        logger.info("PR作成成功");

        return "redirect:/purchase-request/{prId}";
    }

    private Map<Integer, List<String>> createDetailErrorMessages(BindingResult bindingResult) {
        Map<Integer, List<String>> errorMap = new LinkedHashMap<Integer, List<String>>();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        for (FieldError fe : fieldErrors) {
            String field = fe.getField();
            if (!field.startsWith("details[")) {
                continue;
            }
            int startIndex = field.indexOf("[") + 1;
            int endIndex = field.indexOf("]");
            Integer key = Integer.parseInt(field.substring(startIndex, endIndex));
            if (errorMap.containsKey(key)) {
                errorMap.get(key).add(fe.getDefaultMessage());
            }
            else {
                List<String> list = new ArrayList<String>();
                list.add(fe.getDefaultMessage());
                errorMap.put(key, list);
            }

        }
        return errorMap;
    }
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
