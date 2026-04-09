package com.example.p2p.controller;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.form.UserSearchForm;
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

    @GetMapping
    public String showUserList(@Valid @ModelAttribute("form") UserSearchForm form,
            BindingResult bindingResult,
            Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            UserSearchForm lastCondition = (UserSearchForm) session.getAttribute(LAST_SEARCH_CONDITION);
            UserSearchForm formToSearch = lastCondition == null ? new UserSearchForm() : lastCondition;
            formToSearch = lastCondition == null ? new UserSearchForm() : lastCondition;
            model.addAttribute("view", userService.searchUsers(formToSearch));
            return "item-list";
        }
        session.setAttribute(LAST_SEARCH_CONDITION, form);
        model.addAttribute("view", userService.searchUsers(form));

        return "item-list";
    }

    @GetMapping("/{userId}")
    public String showRoleDetail(@PathVariable String userId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("user", userService.getUserDetail(userId));

        return "user-detail";
    }

    // @GetMapping("/create")
    // public String showUnitCreateForm(@ModelAttribute("form") UnitCreateForm form) {
    // return "unit-create";
    // }
    //
    // @PostMapping("/create")
    // public String create(@Valid @ModelAttribute("form") UnitCreateForm form,
    // BindingResult result,
    // RedirectAttributes redirectAttributes) {
    // if (result.hasErrors()) {
    // return "unit-create";
    // }
    // try {
    // unitService.create(form.getName());
    // }
    // catch (BusinessException e) {
    // result.rejectValue("name", "duplicate",
    // messageSource.getMessage("common.duplicate", null, null));
    // return "unit-create";
    // }
    // redirectAttributes.addFlashAttribute("successMessage",
    // messageSource.getMessage("common.create.success", null, null));
    // return "redirect:/setting/unit";
    // }
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
