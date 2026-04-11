package com.example.p2p.controller;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
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
import com.example.p2p.dto.ItemEditViewDto;
import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.form.ItemUpsertForm;
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

    private ModelMapper modelMapper;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @InitBinder("keyword")
    public void initSearchBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new NormalizationEditor());
    }

    @GetMapping
    public String showItemList(@Valid @ModelAttribute("form") ItemSearchForm form,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {
        if (bindingResult.hasErrors()) {
            ItemSearchForm lastCondition = (ItemSearchForm) session.getAttribute(LAST_SEARCH_CONDITION);
            ItemSearchForm formToSearch = lastCondition == null ? new ItemSearchForm() : lastCondition;
            formToSearch = lastCondition == null ? new ItemSearchForm() : lastCondition;
            model.addAttribute("view", itemService.searchItems(formToSearch));
            return "item-list";
        }
        session.setAttribute(LAST_SEARCH_CONDITION, form);
        model.addAttribute("view", itemService.searchItems(form));

        return "item-list";
    }

    @GetMapping("/{itemId}")
    public String showItemDetail(@PathVariable String itemId, Model model) {
        model.addAttribute("itemId", itemId);
        model.addAttribute("detail", itemService.getItemDetail(itemId));
        return "item-detail";
    }

    @GetMapping("/create")
    public String showItemCreateForm(@ModelAttribute("form") ItemUpsertForm form, Model model) {
        Map<String, List> options = itemService.getOptions();
        model.addAttribute("unitOptions", options.get("unitOptions"));
        model.addAttribute("supplierOptions", options.get("supplierOptions"));
        return "item-create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") ItemUpsertForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, List> options = itemService.getOptions();
            model.addAttribute("unitOptions", options.get("unitOptions"));
            model.addAttribute("supplierOptions", options.get("supplierOptions"));
            return "item-create";
        }
        itemService.create(form);

        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.create.success", null, null));
        return "redirect:/setting/item";
    }

    @GetMapping("/{itemId}/edit")
    public String showItemEditForm(@PathVariable String itemId,
            @ModelAttribute("form") ItemUpsertForm form,
            Model model) {
        ItemEditViewDto view = itemService.prepareItemEditView(itemId);
        modelMapper.map(view, form);
        model.addAttribute("unitOptions", view.getUnitOptions());
        model.addAttribute("supplierOptions", view.getSupplierOptions());
        model.addAttribute("itemId", itemId);
        return "item-edit";
    }

    @PostMapping("/{itemId}/edit")
    public String update(@PathVariable String itemId,
            @Valid @ModelAttribute("form") ItemUpsertForm form,
            BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, List> options = itemService.getOptions();
            model.addAttribute("itemId", itemId);
            model.addAttribute("unitOptions", options.get("unitOptions"));
            model.addAttribute("supplierOptions", options.get("supplierOptions"));
            return "item-edit";
        }
        itemService.update(itemId, form);
        redirectAttributes.addAttribute("itemId", itemId);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.update.success", null, null));

        return "redirect:/setting/item/{itemId}";
    }

}
