package com.example.p2p.controller;

import java.util.List;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.advice.NormalizationEditor;
import com.example.p2p.dto.RoleOptionDto;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UnitCreateForm;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.form.UserUpsertForm;
import com.example.p2p.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    private MessageSource messageSource;

    private static final String LAST_SEARCH_CONDITION = "lastSearchCondition";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @InitBinder("keyword")
    public void initSearchBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new NormalizationEditor());
    }

    @GetMapping
    public String showUserList(@Valid @ModelAttribute("form") UserSearchForm form, 
            BindingResult bindingResult,
            Model model,
            HttpSession session) {
        if (bindingResult.hasErrors()) {
            UserSearchForm lastCondition = (UserSearchForm) session.getAttribute(LAST_SEARCH_CONDITION);
            UserSearchForm formToSearch = lastCondition == null ? new UserSearchForm() : lastCondition;
            formToSearch = lastCondition == null ? new UserSearchForm() : lastCondition;
            model.addAttribute("view", userService.searchUsers(formToSearch));
            return "user-list";
        }
        session.setAttribute(LAST_SEARCH_CONDITION, form);
        model.addAttribute("view", userService.searchUsers(form));

        return "user-list";
    }

    @GetMapping("/{userId}")
    public String showRoleDetail(@PathVariable String userId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("user", userService.getUserDetail(userId));

        return "user-detail";
    }

    @GetMapping("/create")
    public String showUserCreateForm(@ModelAttribute("form") UserUpsertForm form, Model model) {
        return "user-create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") UserUpsertForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user-create";
        }
        try {
            userService.create(form);
        }
        catch (BusinessException e) {
            result.rejectValue("email", "duplicate", messageSource.getMessage("common.duplicate", null, null));
            return "user-create";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.create.success", null, null));
        return "redirect:/setting/user";
    }
    
    @ModelAttribute("roleOptions")
    public List<RoleOptionDto> addRoleOptions() {
        return userService.getRoleOptions();
    }
    //
    // @GetMapping("/{unitId}")
    // public String showUnitEditForm(@PathVariable String unitId, @ModelAttribute("form")
    // UnitEditForm form,
    // Model model) {
    // UnitDetailDto unit = unitService.getUnitDetail(unitId);
    // form.setName(unit.getName());
    // form.setStatus(unit.isActive());
    //
    // model.addAttribute("unitId", unitId);
    //
    // return "unit-edit";
    // }
    //
    // @PostMapping("/{unitId}/update")
    // public String update(@PathVariable String unitId, @Valid @ModelAttribute("form")
    // UnitEditForm form,
    // BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    // if (bindingResult.hasErrors()) {
    // model.addAttribute("unitId", unitId);
    // return "unit-edit";
    // }
    // try {
    // unitService.update(unitId, form);
    // }
    // catch (BusinessException e) {
    // model.addAttribute("unitId", unitId);
    // bindingResult.rejectValue("name", "duplicate",
    // messageSource.getMessage("common.duplicate", null, null));
    // return "unit-edit";
    // }
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("common.update.success", null, null));
    // redirectAttributes.addAttribute("unitId", unitId);
    // return "redirect:/setting/unit/{unitId}";
    //
    // }

}
