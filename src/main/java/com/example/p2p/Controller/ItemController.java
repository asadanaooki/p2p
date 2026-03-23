package com.example.p2p.controller;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.p2p.service.ItemService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/item")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;

    private MessageSource messageSource;

    @GetMapping
    public String showItem(@RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "2") int size,
            Model model) {
        model.addAttribute("items", itemService.getItems(page, size));
        return "item-list";
    }

//    @GetMapping("/create")
//    public String showUnitCreateForm(@ModelAttribute("form") UnitCreateForm form) {
//        return "unit-create";
//    }
//
//    @PostMapping("/create")
//    public String create(@Valid @ModelAttribute("form") UnitCreateForm form, BindingResult result,
//            RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            return "unit-create";
//        }
//        try {
//            unitService.create(form.getName());
//        }
//        catch (BusinessException e) {
//            result.rejectValue("name", "duplicate",
//                    messageSource.getMessage("common.duplicate", null, null));
//            return "unit-create";
//        }
//        redirectAttributes.addFlashAttribute("successMessage",
//                messageSource.getMessage("common.create.success", null, null));
//        return "redirect:/setting/unit";
//    }
//
//    @GetMapping("/{unitId}")
//    public String showUnitEditForm(@PathVariable String unitId, @ModelAttribute("form") UnitEditForm form,
//            Model model) {
//        UnitDetailDto unit = unitService.getUnitDetail(unitId);
//        form.setName(unit.getName());
//        form.setStatus(unit.isActive());
//
//        model.addAttribute("unitId", unitId);
//
//        return "unit-edit";
//    }
//
//    @PostMapping("/{unitId}/update")
//    public String update(@PathVariable String unitId, @Valid @ModelAttribute("form") UnitEditForm form,
//            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("unitId", unitId);
//            return "unit-edit";
//        }
//        try {
//            unitService.update(unitId, form);
//        }
//        catch (BusinessException e) {
//            model.addAttribute("unitId", unitId);
//            bindingResult.rejectValue("name", "duplicate",
//                    messageSource.getMessage("common.duplicate", null, null));
//            return "unit-edit";
//        }
//        redirectAttributes.addFlashAttribute("successMessage",
//                messageSource.getMessage("common.update.success", null, null));
//        redirectAttributes.addAttribute("unitId", unitId);
//        return "redirect:/setting/unit/{unitId}";
//
//    }

}
