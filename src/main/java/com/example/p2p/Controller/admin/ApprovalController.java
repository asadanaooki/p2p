package com.example.p2p.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/approval")
@AllArgsConstructor
public class ApprovalController {

//    private RoleService roleService;
//
//    private MessageSource messageSource;
//    
//    private ModelMapper modelMapper;
//
    @GetMapping("/document")
    public String showApprovalDocumentSelection() {
        return "admin/approval-document-select";
    }
//
//    @GetMapping("/{roleId}")
//    public String showRoleDetail(@PathVariable String roleId, Model model) {
//        model.addAttribute("roleId", roleId);
//        model.addAttribute("role", roleService.getRoleDetail(roleId));
//
//        return "admin/role-detail";
//    }
//
//     @GetMapping("/create")
//     public String showRoleCreateForm(@ModelAttribute("form") RoleUpsertForm form) {
//         form.setPrViewScope(VisibilityScope.NONE);
//         form.setPoViewScope(VisibilityScope.NONE);
//         form.setReceiptViewScope(VisibilityScope.NONE);
//         form.setInvoiceViewScope(VisibilityScope.NONE);
//         return "admin/role-create";
//     }
//     
//    @PostMapping("/create")
//    public String create(@Valid @ModelAttribute("form") RoleUpsertForm form, BindingResult result,
//            RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            return "admin/role-create";
//        }
//        try {
//            roleService.create(form);
//        }
//        catch (BusinessException e) {
//            result.rejectValue("name", "duplicate",
//                    messageSource.getMessage("common.duplicate", null, null));
//            return "admin/role-create";
//        }
//        redirectAttributes.addFlashAttribute("successMessage",
//                messageSource.getMessage("common.create.success", null, null));
//        return "redirect:/setting/role";
//    }
//
//    
//    @GetMapping("/{roleId}/edit")
//    public String showRoleEditForm(@PathVariable String roleId, @ModelAttribute("form") RoleUpsertForm form,
//            Model model) {
//        RoleDetailDto role = roleService.getRoleDetail(roleId);
//        modelMapper.map(role, form);
//        model.addAttribute("roleId", roleId);
//
//        return "admin/role-edit";
//    }
//    
//    @PostMapping("/{roleId}/update")
//    public String update(@PathVariable String roleId,
//            @Valid @ModelAttribute("form") RoleUpsertForm form,
//            BindingResult bindingResult,
//            Model model,
//            RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("roleId", roleId);
//            return "admin/role-edit";
//        }
//        try {
//            roleService.update(roleId, form);
//        }
//        catch (BusinessException e) {
//            model.addAttribute("roleId", roleId);
//            bindingResult.rejectValue("name", "duplicate",
//                    messageSource.getMessage("common.duplicate", null, null));
//            return "admin/role-edit";
//        }
//        redirectAttributes.addFlashAttribute("successMessage",
//                messageSource.getMessage("common.update.success", null, null));
//        redirectAttributes.addAttribute("roleId", roleId);
//        return "redirect:/setting/role/{roleId}";
//    }
}
