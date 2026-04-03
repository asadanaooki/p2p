package com.example.p2p.controller;

import org.modelmapper.ModelMapper;
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

import com.example.p2p.dto.RoleDetailDto;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.RoleUpsertForm;
import com.example.p2p.service.RoleService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/role")
@AllArgsConstructor
public class RoleController {

    private RoleService roleService;

    private MessageSource messageSource;
    
    private ModelMapper modelMapper;

    @GetMapping
    public String showRoleList(Model model) {
        model.addAttribute("roleList", roleService.getRoleList());
        return "role-list";
    }

    @GetMapping("/{roleId}")
    public String showRoleDetail(@PathVariable String roleId, Model model) {
        model.addAttribute("roleId", roleId);
        model.addAttribute("role", roleService.getRoleDetail(roleId));

        return "role-detail";
    }

     @GetMapping("/create")
     public String showRoleCreateForm(@ModelAttribute("form") RoleUpsertForm form) {
         form.setPrViewScope(VisibilityScope.NONE);
         form.setPoViewScope(VisibilityScope.NONE);
         form.setReceiptViewScope(VisibilityScope.NONE);
         form.setInvoiceViewScope(VisibilityScope.NONE);
         return "role-create";
     }
     
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") RoleUpsertForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "role-create";
        }
        try {
            roleService.create(form);
        }
        catch (BusinessException e) {
            result.rejectValue("name", "duplicate",
                    messageSource.getMessage("common.duplicate", null, null));
            return "role-create";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.create.success", null, null));
        return "redirect:/setting/role";
    }

    
    @GetMapping("/{roleId}/edit")
    public String showRoleEditForm(@PathVariable String roleId, @ModelAttribute("form") RoleUpsertForm form,
            Model model) {
        RoleDetailDto role = roleService.getRoleDetail(roleId);
        modelMapper.map(role, form);
        model.addAttribute("roleId", roleId);

        return "role-edit";
    }
    
    @PostMapping("/{roleId}/update")
    public String update(@PathVariable String roleId,
            @Valid @ModelAttribute("form") RoleUpsertForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roleId", roleId);
            return "role-edit";
        }
        try {
            roleService.update(roleId, form);
        }
        catch (BusinessException e) {
            model.addAttribute("roleId", roleId);
            bindingResult.rejectValue("name", "duplicate",
                    messageSource.getMessage("common.duplicate", null, null));
            return "role-edit";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.update.success", null, null));
        redirectAttributes.addAttribute("roleId", roleId);
        return "redirect:/setting/role/{roleId}";
    }
}
