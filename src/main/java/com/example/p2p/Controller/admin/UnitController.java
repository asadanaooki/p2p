package com.example.p2p.controller.admin;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.dto.UnitDetailDto;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UnitCreateForm;
import com.example.p2p.form.UnitEditForm;
import com.example.p2p.service.admin.UnitService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/unit")
@AllArgsConstructor
public class UnitController {

    private UnitService unitService;

    private MessageSource messageSource;

    @GetMapping
    public String showUnitList(Model model, @RequestParam(required = false) Boolean status) {
        model.addAttribute("status", status);
        model.addAttribute("unitList", unitService.getUnitList(status));
        return "admin/unit-list";
    }

    @GetMapping("/create")
    public String showUnitCreateForm(@ModelAttribute("form") UnitCreateForm form) {
        return "admin/unit-create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") UnitCreateForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/unit-create";
        }
        try {
            unitService.create(form.getName());
        }
        catch (BusinessException e) {
            result.rejectValue("name", "duplicate",
                    messageSource.getMessage("common.duplicate", null, null));
            return "admin/unit-create";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.create.success", null, null));
        return "redirect:/setting/unit";
    }

    @GetMapping("/{unitId}")
    public String showUnitEditForm(@PathVariable String unitId, @ModelAttribute("form") UnitEditForm form,
            Model model) {
        UnitDetailDto unit = unitService.getUnitDetail(unitId);
        form.setName(unit.getName());
        form.setStatus(unit.isActive());

        model.addAttribute("unitId", unitId);

        return "admin/unit-edit";
    }

    @PostMapping("/{unitId}/update")
    public String update(@PathVariable String unitId, @Valid @ModelAttribute("form") UnitEditForm form,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("unitId", unitId);
            return "admin/unit-edit";
        }
        try {
            unitService.update(unitId, form);
        }
        catch (BusinessException e) {
            model.addAttribute("unitId", unitId);
            bindingResult.rejectValue("name", "duplicate",
                    messageSource.getMessage("common.duplicate", null, null));
            return "admin/unit-edit";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.update.success", null, null));
        redirectAttributes.addAttribute("unitId", unitId);
        return "redirect:/setting/unit/{unitId}";

    }

}
