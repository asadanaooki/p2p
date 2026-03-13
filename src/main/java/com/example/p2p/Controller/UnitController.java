package com.example.p2p.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UnitCreateForm;
import com.example.p2p.service.UnitService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/unit")
@AllArgsConstructor
public class UnitController {

    private UnitService unitService;

    @GetMapping
    public String showUnit(Model model, @RequestParam(required = false) Boolean status) {
        model.addAttribute("status", status);
        model.addAttribute("unitList", unitService.getUnitList(status));
        return "unit-list";
    }

    @GetMapping("/create")
    public String showUnitCreateForm(@ModelAttribute("form") UnitCreateForm form, Model model) {
        model.addAttribute("form", form);
        return "unit-create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") UnitCreateForm form, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "unit-create";
        }
        // TODO: 例外処理増えたら共通化
        try {
            unitService.create(form.getName());
        }
        catch (BusinessException e) {
            model.addAttribute("form", form);
            result.rejectValue("name", "duplicate", "既に登録されています");
            return "unit-create";
        }
        redirectAttributes.addFlashAttribute("successMessage", "登録成功しました");
        return "redirect:/setting/unit";
    }

}
