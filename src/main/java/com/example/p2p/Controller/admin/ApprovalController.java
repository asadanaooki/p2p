package com.example.p2p.controller.admin;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.dto.admin.ApprovalWorkflowDto;
import com.example.p2p.enums.DocumentType;
import com.example.p2p.form.admin.ApprovalWorkflowCreateForm;
import com.example.p2p.service.admin.ApprovalService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/approval")
@AllArgsConstructor
public class ApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalController.class);

    private ApprovalService approvalService;

    private MessageSource messageSource;

    private ModelMapper modelMapper;

    @GetMapping("/document")
    public String showApprovalDocumentSelection() {
        logger.debug("承認対象選択画面表示開始");

        logger.debug("承認対象選択画面表示完了");
        return "admin/approval-document-select";
    }

    @GetMapping("/{documentType}/workflow")
    public String showWorkflow(@PathVariable DocumentType documentType,
            @ModelAttribute("form") ApprovalWorkflowCreateForm form, Model model) {
        logger.debug("承認ワークフロー画面表示開始");

        ApprovalWorkflowDto dto = approvalService.getApprovalWorkflowView(documentType);
        modelMapper.map(dto, form);
        model.addAttribute("documentType", documentType);
        model.addAttribute("userOptions", dto.getUserOptions());

        logger.debug("承認ワークフロー画面表示完了");
        return "admin/approval-workflow";
    }

    @PostMapping("/{documentType}/workflow")
    public String createWorkflow(@PathVariable DocumentType documentType,
            @Valid @ModelAttribute("form") ApprovalWorkflowCreateForm form, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("承認ワークフロー登録開始");

        if (bindingResult.hasErrors()) {
            logger.warn("承認ワークフロー登録の入力エラー: errorCount={}", bindingResult.getErrorCount());
            model.addAttribute("documentType", documentType);
            model.addAttribute("userOptions", approvalService.getApprovalUserOptions(documentType));
            return "admin/approval-workflow";
        }
        approvalService.create(documentType, form);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.save.success", null, null));
        redirectAttributes.addAttribute("documentType", documentType);

        logger.info("承認ワークフロー登録成功");
        return "redirect:/setting/approval/{documentType}/workflow";
    }
    //
    // @GetMapping("/{roleId}")
    // public String showRoleDetail(@PathVariable String roleId, Model model) {
    // model.addAttribute("roleId", roleId);
    // model.addAttribute("role", roleService.getRoleDetail(roleId));
    //
    // return "admin/role-detail";
    // }
    //
    // @GetMapping("/create")
    // public String showRoleCreateForm(@ModelAttribute("form") RoleUpsertForm form) {
    // form.setPrViewScope(VisibilityScope.NONE);
    // form.setPoViewScope(VisibilityScope.NONE);
    // form.setReceiptViewScope(VisibilityScope.NONE);
    // form.setInvoiceViewScope(VisibilityScope.NONE);
    // return "admin/role-create";
    // }
    //
    // @PostMapping("/create")
    // public String create(@Valid @ModelAttribute("form") RoleUpsertForm form,
    // BindingResult result,
    // RedirectAttributes redirectAttributes) {
    // if (result.hasErrors()) {
    // return "admin/role-create";
    // }
    // try {
    // roleService.create(form);
    // }
    // catch (BusinessException e) {
    // result.rejectValue("name", "duplicate",
    // messageSource.getMessage("common.duplicate", null, null));
    // return "admin/role-create";
    // }
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("common.create.success", null, null));
    // return "redirect:/setting/role";
    // }
    //
    //
    // @GetMapping("/{roleId}/edit")
    // public String showRoleEditForm(@PathVariable String roleId, @ModelAttribute("form")
    // RoleUpsertForm form,
    // Model model) {
    // RoleDetailDto role = roleService.getRoleDetail(roleId);
    // modelMapper.map(role, form);
    // model.addAttribute("roleId", roleId);
    //
    // return "admin/role-edit";
    // }
    //
    // @PostMapping("/{roleId}/update")
    // public String update(@PathVariable String roleId,
    // @Valid @ModelAttribute("form") RoleUpsertForm form,
    // BindingResult bindingResult,
    // Model model,
    // RedirectAttributes redirectAttributes) {
    // if (bindingResult.hasErrors()) {
    // model.addAttribute("roleId", roleId);
    // return "admin/role-edit";
    // }
    // try {
    // roleService.update(roleId, form);
    // }
    // catch (BusinessException e) {
    // model.addAttribute("roleId", roleId);
    // bindingResult.rejectValue("name", "duplicate",
    // messageSource.getMessage("common.duplicate", null, null));
    // return "admin/role-edit";
    // }
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("common.update.success", null, null));
    // redirectAttributes.addAttribute("roleId", roleId);
    // return "redirect:/setting/role/{roleId}";
    // }

}
