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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.dto.PaymentTermDetailDto;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.PaymentTermCreateForm;
import com.example.p2p.form.PaymentTermEditForm;
import com.example.p2p.service.admin.PaymentTermService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/payment-term")
@AllArgsConstructor
public class PaymentTermController {

    private PaymentTermService paymentTermService;

    private MessageSource messageSource;

    @GetMapping
    public String showPaymentTermList(Model model) {
        model.addAttribute("list", paymentTermService.getPaymentTerms());
        return "payment-term-list";
    }

    @GetMapping("/{paymentTermId}")
    public String showPaymentTermEditForm(@PathVariable String paymentTermId,
            @ModelAttribute("form") PaymentTermEditForm form, Model model) {
        PaymentTermDetailDto pt = paymentTermService.getPaymentTermDetail(paymentTermId);
        form.setName(pt.getName());
        form.setDays(pt.getDays());
        form.setDueDateType(pt.getDueDateType());
        form.setActive(pt.isActive());
        model.addAttribute("paymentTermId", paymentTermId);
        
        return "payment-term-edit";
    }
    
    @GetMapping("/create")
    public String showPaymentTermCreateForm(@ModelAttribute("form") PaymentTermCreateForm form) {
        return "payment-term-create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") PaymentTermCreateForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "payment-term-create";
        }
        try {
            paymentTermService.create(form);
        }
        catch (BusinessException e) {
            result.rejectValue("name", "duplicate",
                    messageSource.getMessage("common.duplicate", null, null));
            return "payment-term-create";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.create.success", null, null));
        return "redirect:/setting/payment-term";
    }

    @PostMapping("/{paymentTermId}/update")
    public String update(@PathVariable String paymentTermId, @Valid @ModelAttribute("form") PaymentTermEditForm form,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("paymentTermId", paymentTermId);
            return "payment-term-edit";
        }
        try {
            paymentTermService.update(paymentTermId, form);
        }
        catch (BusinessException e) {
            model.addAttribute("paymentTermId", paymentTermId);
            bindingResult.rejectValue("name", "duplicate", messageSource.getMessage("common.duplicate", null, null));
            return "payment-term-edit";
        }
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.update.success", null, null));
        redirectAttributes.addAttribute("paymentTermId", paymentTermId);
        return "redirect:/setting/payment-term/{paymentTermId}";

    }

}
