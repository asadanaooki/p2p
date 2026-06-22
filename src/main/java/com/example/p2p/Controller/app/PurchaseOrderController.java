package com.example.p2p.controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.p2p.form.app.PurchaseOrderSearchForm;
import com.example.p2p.security.CustomUserDetails;
import com.example.p2p.service.app.PurchaseOrderService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/purchase-order")
@AllArgsConstructor
public class PurchaseOrderController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);

    private static final String LAST_SEARCH_CONDITION = "lastSearchCondition";

    private PurchaseOrderService purchaseOrderService;

    // private MessageSource messageSource;
    //
    // private static final Map<String, Integer> sortMap =
    // Map.ofEntries(Map.entry("nonForm", 0),
    // Map.entry("detailInputType", 1), Map.entry("itemId", 2), Map.entry("itemName", 3),
    // Map.entry("kind", 4),
    // Map.entry("supplierId", 5), Map.entry("supplierName", 6), Map.entry("unitId", 7),
    // Map.entry("unitName", 8),
    // Map.entry("price", 9), Map.entry("quantity", 10));

    // private ApprovalActionService approvalActionService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @PreAuthorize("hasAuthority('PO_VIEW_ALL') or hasAuthority('PO_VIEW_SELF')")
    @GetMapping
    public String showPurchaseOrderList(@Valid @ModelAttribute("form") PurchaseOrderSearchForm form,
            BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails loginUser, Model model,
            HttpSession session) {
        logger.debug("発注一覧画面表示開始");

        if (bindingResult.hasErrors()) {
            PurchaseOrderSearchForm lastCondition = (PurchaseOrderSearchForm) session
                .getAttribute(LAST_SEARCH_CONDITION);
            PurchaseOrderSearchForm formToSearch = lastCondition == null ? new PurchaseOrderSearchForm()
                    : lastCondition;
            model.addAttribute("view", purchaseOrderService.searchPurchaseOrders(formToSearch, loginUser));
            return "app/purchase-order-list";
        }
        session.setAttribute(LAST_SEARCH_CONDITION, form);
        model.addAttribute("view", purchaseOrderService.searchPurchaseOrders(form, loginUser));

        logger.debug("発注一覧画面表示完了");
        return "app/purchase-order-list";
    }

    // @PreAuthorize("hasAuthority('PR_VIEW_ALL') or hasAuthority('PR_VIEW_SELF')")
    // @GetMapping("/{prId}")
    // public String showPurchaseRequestDetail(@PathVariable @NotBlank String prId,
    // @AuthenticationPrincipal CustomUserDetails loginUser, Model model) {
    // logger.debug("PR詳細画面表示開始");
    //
    // model.addAttribute("view", purchaseRequestService.getPurchaseRequestDetail(prId,
    // loginUser));
    //
    // logger.debug("PR詳細画面表示完了");
    // return "app/purchase-request-detail";
    // }
    //
    // @PreAuthorize("hasAuthority('PR_CREATE')")
    // @GetMapping("/create")
    // public String showPurchaseRequestCreateForm(@AuthenticationPrincipal(expression =
    // "username") String userId,
    // @ModelAttribute("form") PurchaseRequestCreateForm form, Model model) {
    // logger.debug("PR作成画面表示開始");
    //
    // model.addAttribute("view", purchaseRequestService.prepareCreateView(userId));
    //
    // logger.debug("PR作成画面表示完了");
    //
    // return "app/purchase-request-create";
    // }
    //
    // @PreAuthorize("hasAuthority('PR_CREATE')")
    // @PostMapping("/create")
    // public String create(@AuthenticationPrincipal(expression = "username") String
    // userId,
    // @Valid @ModelAttribute("form") PurchaseRequestCreateForm form, BindingResult
    // bindingResult, Model model,
    // RedirectAttributes redirectAttributes) throws ServletException {
    // logger.info("PR作成開始");
    //
    // if (bindingResult.hasErrors()) {
    // logger.warn("PR作成バリデーションエラー");
    //
    // model.addAttribute("view", purchaseRequestService.prepareCreateView(userId));
    // model.addAttribute("detailErrorMessages",
    // createDetailErrorMessages(bindingResult));
    // return "app/purchase-request-create";
    // }
    //
    // String prId = null;
    // try {
    // prId = purchaseRequestService.create(userId, form);
    // }
    // catch (BusinessException e) {
    // logger.warn("PR作成不可");
    // model.addAttribute("view", purchaseRequestService.prepareCreateView(userId));
    // model.addAttribute("approvalTaskNotFoundMessage",
    // messageSource.getMessage("purchaseRequest.approver.notFound", null, null));
    // return "app/purchase-request-create";
    // }
    //
    // redirectAttributes.addAttribute("prId", prId);
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("purchaseRequest.create.success", null, null));
    //
    // logger.info("PR作成成功");
    //
    // return "redirect:/purchase-request/{prId}";
    // }
    //
    // @PreAuthorize("hasAuthority('PR_CREATE')")
    // @GetMapping("/{prId}/edit")
    // public String showPurchaseRequestEditForm(@PathVariable @NotBlank String prId,
    // @ModelAttribute("form") PurchaseRequestEditForm form, Model model) {
    // logger.debug("PR編集画面表示開始");
    //
    // PurchaseRequestEditViewDto view = purchaseRequestService.prepareEditView(prId);
    // form.setDueDate(view.getDueDate());
    // form.setNote(view.getNote());
    //
    // model.addAttribute("prId", prId);
    // model.addAttribute("view", view);
    //
    // logger.debug("PR編集画面表示完了");
    //
    // return "app/purchase-request-edit";
    // }
    //
    // @PreAuthorize("hasAuthority('PR_CREATE')")
    // @PostMapping("/{prId}/edit")
    // public String update(@PathVariable @NotBlank String prId,
    // @Valid @ModelAttribute("form") PurchaseRequestEditForm form, BindingResult
    // bindingResult, Model model,
    // RedirectAttributes redirectAttributes) throws ServletException {
    // logger.info("PR編集開始");
    //
    // if (bindingResult.hasErrors()) {
    // logger.warn("PR編集バリデーションエラー");
    //
    // model.addAttribute("view", purchaseRequestService.prepareEditView(prId));
    // model.addAttribute("detailErrorMessages",
    // createDetailErrorMessages(bindingResult));
    // return "app/purchase-request-edit";
    // }
    // try {
    // purchaseRequestService.update(prId, form);
    // }
    // catch (BusinessException e) {
    // logger.warn("PR編集不可");
    // model.addAttribute("view", purchaseRequestService.prepareEditView(prId));
    // model.addAttribute("errorMessage",
    // messageSource.getMessage("purchaseRequest.edit.notAllowed", null, null));
    //
    // return "app/purchase-request-edit";
    // }
    //
    // redirectAttributes.addAttribute("prId", prId);
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("purchaseRequest.edit.success", null, null));
    //
    // logger.info("PR編集成功");
    //
    // return "redirect:/purchase-request/{prId}";
    // }
    //
    // @PreAuthorize("hasAuthority('PR_CREATE')")
    // @PostMapping("/{prId}/void")
    // public String cancel(@PathVariable String prId, @RequestParam(required = false)
    // String reason,
    // RedirectAttributes redirectAttributes) {
    // logger.info("PR無効化開始");
    //
    // redirectAttributes.addAttribute("prId", prId);
    // try {
    // purchaseRequestService.cancel(prId, reason);
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("purchaseRequest.void.success", null, null));
    //
    // logger.info("PR無効化成功");
    // }
    // catch (BusinessException e) {
    // logger.warn("PR無効化不可");
    // redirectAttributes.addFlashAttribute("errorMessage",
    // messageSource.getMessage("purchaseRequest.void.notAllowed", null, null));
    // }
    // return "redirect:/purchase-request/{prId}";
    //
    // }
    //
    // @PreAuthorize("hasAuthority('PR_APPROVE')")
    // @PostMapping("/{prId}/approve")
    // public String approve(@PathVariable String prId,
    // @AuthenticationPrincipal(expression = "username") String userId,
    // RedirectAttributes redirectAttributes) {
    // logger.info("PR承認開始");
    //
    // redirectAttributes.addAttribute("prId", prId);
    // try {
    // approvalActionService.approvePR(prId, userId);
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("purchaseRequest.approve.success", null, null));
    //
    // logger.info("PR承認成功");
    // }
    // catch (BusinessException e) {
    // logger.warn("PR承認不可: {}", e.getErrorCode());
    //
    // redirectAttributes.addFlashAttribute("errorMessage",
    // messageSource.getMessage("purchaseRequest.approve.notAllowed", null, null));
    // }
    // return "redirect:/purchase-request/{prId}";
    //
    // }
    //
    // @PreAuthorize("hasAuthority('PR_APPROVE')")
    // @PostMapping("/{prId}/reject")
    // public String reject(@PathVariable String prId, @AuthenticationPrincipal(expression
    // = "username") String userId,
    // @RequestParam(required = false) String reason, RedirectAttributes
    // redirectAttributes) {
    // logger.info("PR否認開始");
    //
    // redirectAttributes.addAttribute("prId", prId);
    // try {
    // approvalActionService.rejectPR(prId, userId, reason);
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("purchaseRequest.reject.success", null, null));
    //
    // logger.info("PR否認成功");
    // }
    // catch (BusinessException e) {
    // logger.warn("PR否認不可: {}", e.getErrorCode());
    //
    // redirectAttributes.addFlashAttribute("errorMessage",
    // messageSource.getMessage("purchaseRequest.reject.notAllowed", null, null));
    // }
    // return "redirect:/purchase-request/{prId}";
    //
    // }
    //
    // private Map<Integer, List<String>> createDetailErrorMessages(BindingResult
    // bindingResult) {
    // Map<Integer, List<String>> errorMap = new LinkedHashMap<Integer, List<String>>();
    // List<FieldError> fieldErrors = bindingResult.getFieldErrors();
    // List<FieldError> sortedFieldErrors = fieldErrors.stream()
    // .filter(f -> f.getField().startsWith("details["))
    // .sorted((f1, f2) -> {
    // // 行明細並び替え
    // int f1RowStartIndex = f1.getField().indexOf("[") + 1;
    // int f1RowEndIndex = f1.getField().indexOf("]");
    // int f2RowStartIndex = f2.getField().indexOf("[") + 1;
    // int f2RowEndIndex = f2.getField().indexOf("]");
    // int rowCompare = Integer.compare(
    // Integer.parseInt(f1.getField().substring(f1RowStartIndex, f1RowEndIndex)),
    // Integer.parseInt(f2.getField().substring(f2RowStartIndex, f2RowEndIndex)));
    //
    // if (rowCompare != 0) {
    // return rowCompare;
    // }
    // // 明細内容並び替え
    // int f1FieldNameStartIndex = f1.getField().indexOf(".") + 1;
    // int f2FieldNameStartIndex = f2.getField().indexOf(".") + 1;
    // return Integer.compare(sortMap.get(f1.getField().substring(f1FieldNameStartIndex)),
    // sortMap.get(f2.getField().substring(f2FieldNameStartIndex)));
    // })
    // .toList();
    //
    // for (FieldError fe : sortedFieldErrors) {
    // String field = fe.getField();
    // int rowStartIndex = field.indexOf("[") + 1;
    // int rowEndIndex = field.indexOf("]");
    // Integer key = Integer.parseInt(field.substring(rowStartIndex, rowEndIndex));
    //
    // errorMap.computeIfAbsent(key, k -> new
    // ArrayList<String>()).add(fe.getDefaultMessage());
    // }
    // return errorMap;
    // }
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
