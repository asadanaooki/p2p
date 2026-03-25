package com.example.p2p.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.service.ItemService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/item")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;

    private MessageSource messageSource;
    
    private static final String LAST_SEARCH_CONDITION = "lastSearchCondition";
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String showItem(@Valid @ModelAttribute("form") ItemSearchForm form,
            BindingResult bindingResult,
            Model model,
            HttpSession session
            ) {
        if (bindingResult.hasErrors()) {
            ItemSearchForm lastCondition = 
                    (ItemSearchForm) session.getAttribute(LAST_SEARCH_CONDITION);
            ItemSearchForm formToSearch = lastCondition == null ? new ItemSearchForm() : lastCondition;
            formToSearch = lastCondition == null ? new ItemSearchForm() : lastCondition;
            model.addAttribute("items", itemService.searchItems(formToSearch));
            return "item-list";
        }
        session.setAttribute(LAST_SEARCH_CONDITION, form);
        model.addAttribute("items", itemService.searchItems(form));
        
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
