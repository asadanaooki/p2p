package com.example.p2p.controller.app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.example.p2p.dto.app.PurchaseRequestEditViewDto;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.app.PurchaseRequestCreateForm;
import com.example.p2p.form.app.PurchaseRequestEditForm;
import com.example.p2p.form.app.PurchaseRequestSearchForm;
import com.example.p2p.security.CustomUserDetails;
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

    private static final Logger logger = LoggerFactory.getLogger(PurchaseRequestController.class);

    private static final String LAST_SEARCH_CONDITION = "lastSearchCondition";

    private PurchaseRequestService purchaseRequestService;

    private MessageSource messageSource;

    private static final Map<String, Integer> sortMap = Map.ofEntries(Map.entry("nonForm", 0),
            Map.entry("detailInputType", 1), Map.entry("itemId", 2), Map.entry("itemName", 3), Map.entry("kind", 4),
            Map.entry("supplierId", 5), Map.entry("supplierName", 6), Map.entry("unitId", 7), Map.entry("unitName", 8),
            Map.entry("price", 9), Map.entry("quantity", 10));

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @PreAuthorize("hasAuthority('PR_VIEW_ALL') or hasAuthority('PR_VIEW_SELF')")
    @GetMapping
    public String showPurchaseRequestList(@Valid @ModelAttribute("form") PurchaseRequestSearchForm form,
            BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails loginUser, Model model,
            HttpSession session) {
        logger.debug("PR一覧画面表示開始");

        if (bindingResult.hasErrors()) {
            PurchaseRequestSearchForm lastCondition = (PurchaseRequestSearchForm) session
                .getAttribute(LAST_SEARCH_CONDITION);
            PurchaseRequestSearchForm formToSearch = lastCondition == null ? new PurchaseRequestSearchForm()
                    : lastCondition;
            model.addAttribute("view", purchaseRequestService.searchPurchaseRequests(formToSearch, loginUser));
            return "app/purchase-request-list";
        }
        session.setAttribute(LAST_SEARCH_CONDITION, form);
        model.addAttribute("view", purchaseRequestService.searchPurchaseRequests(form, loginUser));

        logger.debug("PR一覧画面表示完了");
        return "app/purchase-request-list";
    }

    @PreAuthorize("hasAuthority('PR_VIEW_ALL') or hasAuthority('PR_VIEW_SELF')")
    @GetMapping("/{prId}")
    public String showPurchaseRequestDetail(@PathVariable @NotBlank String prId,
            @AuthenticationPrincipal CustomUserDetails loginUser, Model model) {
        logger.debug("PR詳細画面表示開始");

        model.addAttribute("view", purchaseRequestService.getPurchaseRequestDetail(prId, loginUser));

        logger.debug("PR詳細画面表示完了");
        return "app/purchase-request-detail";
    }

    @PreAuthorize("hasAuthority('PR_CREATE')")
    @GetMapping("/create")
    public String showPurchaseRequestCreateForm(@AuthenticationPrincipal(expression = "username") String userId,
            @ModelAttribute("form") PurchaseRequestCreateForm form, Model model) {
        logger.debug("PR作成画面表示開始");

        model.addAttribute("view", purchaseRequestService.prepareCreateView(userId));

        logger.debug("PR作成画面表示完了");

        return "app/purchase-request-create";
    }

    @PreAuthorize("hasAuthority('PR_CREATE')")
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

        String prId = null;
        try {
            prId = purchaseRequestService.create(userId, form);
        }
        catch (BusinessException e) {
            logger.warn("PR作成不可");
            model.addAttribute("view", purchaseRequestService.prepareCreateView(userId));
            model.addAttribute("approvalTaskNotFoundMessage",
                    messageSource.getMessage("purchaseRequest.approver.notFound", null, null));
            return "app/purchase-request-create";
        }

        redirectAttributes.addAttribute("prId", prId);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("purchaseRequest.create.success", null, null));

        logger.info("PR作成成功");

        return "redirect:/purchase-request/{prId}";
    }

    @PreAuthorize("hasAuthority('PR_CREATE')")
    @GetMapping("/{prId}/edit")
    public String showPurchaseRequestEditForm(@PathVariable @NotBlank String prId,
            @ModelAttribute("form") PurchaseRequestEditForm form, Model model) {
        logger.debug("PR編集画面表示開始");

        PurchaseRequestEditViewDto view = purchaseRequestService.prepareEditView(prId);
        form.setDueDate(view.getDueDate());
        form.setNote(view.getNote());

        model.addAttribute("prId", prId);
        model.addAttribute("view", view);

        logger.debug("PR編集画面表示完了");

        return "app/purchase-request-edit";
    }

    @PreAuthorize("hasAuthority('PR_CREATE')")
    @PostMapping("/{prId}/edit")
    public String update(@PathVariable @NotBlank String prId,
            @Valid @ModelAttribute("form") PurchaseRequestEditForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) throws ServletException {
        logger.info("PR編集開始");

        if (bindingResult.hasErrors()) {
            logger.warn("PR編集バリデーションエラー");

            model.addAttribute("view", purchaseRequestService.prepareEditView(prId));
            model.addAttribute("detailErrorMessages", createDetailErrorMessages(bindingResult));
            return "app/purchase-request-edit";
        }
        try {
            purchaseRequestService.update(prId, form);
        }
        catch (BusinessException e) {
            logger.warn("PR編集不可");
            model.addAttribute("view", purchaseRequestService.prepareEditView(prId));
            model.addAttribute("invalidStatusMessage",
                    messageSource.getMessage("purchaseRequest.edit.notAllowed", null, null));

            return "app/purchase-request-edit";
        }

        redirectAttributes.addAttribute("prId", prId);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("purchaseRequest.edit.success", null, null));

        logger.info("PR編集成功");

        return "redirect:/purchase-request/{prId}";
    }

    private Map<Integer, List<String>> createDetailErrorMessages(BindingResult bindingResult) {
        Map<Integer, List<String>> errorMap = new LinkedHashMap<Integer, List<String>>();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<FieldError> sortedFieldErrors = fieldErrors.stream()
            .filter(f -> f.getField().startsWith("details["))
            .sorted((f1, f2) -> {
                // 行明細並び替え
                int f1RowStartIndex = f1.getField().indexOf("[") + 1;
                int f1RowEndIndex = f1.getField().indexOf("]");
                int f2RowStartIndex = f2.getField().indexOf("[") + 1;
                int f2RowEndIndex = f2.getField().indexOf("]");
                int rowCompare = Integer.compare(
                        Integer.parseInt(f1.getField().substring(f1RowStartIndex, f1RowEndIndex)),
                        Integer.parseInt(f2.getField().substring(f2RowStartIndex, f2RowEndIndex)));

                if (rowCompare != 0) {
                    return rowCompare;
                }
                // 明細内容並び替え
                int f1FieldNameStartIndex = f1.getField().indexOf(".") + 1;
                int f2FieldNameStartIndex = f2.getField().indexOf(".") + 1;
                return Integer.compare(sortMap.get(f1.getField().substring(f1FieldNameStartIndex)),
                        sortMap.get(f2.getField().substring(f2FieldNameStartIndex)));
            })
            .toList();

        for (FieldError fe : sortedFieldErrors) {
            String field = fe.getField();
            int rowStartIndex = field.indexOf("[") + 1;
            int rowEndIndex = field.indexOf("]");
            Integer key = Integer.parseInt(field.substring(rowStartIndex, rowEndIndex));

            errorMap.computeIfAbsent(key, k -> new ArrayList<String>()).add(fe.getDefaultMessage());
        }
        return errorMap;
    }
    //

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
