package com.example.p2p.controller.admin;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.dto.admin.SupplierDetailDto;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.admin.SupplierCreateForm;
import com.example.p2p.form.admin.SupplierEditForm;
import com.example.p2p.form.admin.UnitCreateForm;
import com.example.p2p.service.admin.SupplierService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/supplier")
@AllArgsConstructor
public class SupplierController {

    private SupplierService supplierService;

    private MessageSource messageSource;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
    
    @GetMapping
    public String showSupplierList(@RequestParam(required = false) Boolean status,
            @RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("list", supplierService.getSuppliers(status, keyword));
        return "admin/supplier-list";
    }

    @GetMapping("/{supplierId}")
    public String showSupplierDetail(@PathVariable String supplierId, Model model) {
        model.addAttribute("supplierId", supplierId);
        model.addAttribute("supplier", supplierService.getSupplierDetail(supplierId));
        return "admin/supplier-detail";
    }

     @GetMapping("/create")
     public String showSupplierCreateForm(@ModelAttribute("form") SupplierCreateForm form, Model model) {
         model.addAttribute("paymentTerms", supplierService.getPaymentTermOptions());
         return "admin/supplier-create";
     }

     @PostMapping("/create")
     public String create(@Valid @ModelAttribute("form") SupplierCreateForm form,
             BindingResult result,
             Model model,
             RedirectAttributes redirectAttributes) {
         if (result.hasErrors()) {
             model.addAttribute("paymentTerms", supplierService.getPaymentTermOptions());
             return "admin/supplier-create";
         }
         supplierService.create(form);
         
         redirectAttributes.addFlashAttribute("successMessage",
                 messageSource.getMessage("common.create.success", null, null));
         return "redirect:/setting/supplier";
     }
    
    @GetMapping("/{supplierId}/edit")
    public String showSupplierEditForm(@PathVariable String supplierId,
            @ModelAttribute("form") SupplierEditForm form,
            Model model) {
        SupplierDetailDto supplier = supplierService.getSupplierDetail(supplierId);
        form.setName(supplier.getSupplierName());
        form.setNameKana(supplier.getSupplierNameKana());
        form.setEmail(supplier.getEmail());
        form.setPhoneNumber(supplier.getPhoneNumber());
        form.setPostalCode(supplier.getPostalCode());
        form.setPrefecture(supplier.getPrefecture());
        form.setCity(supplier.getCity());
        form.setStreetAddress(supplier.getStreetAddress());
        form.setBuildingName(supplier.getBuildingName());
        form.setPaymentTermId(supplier.getPaymentTermId());
        form.setStatus(supplier.isActive());

        model.addAttribute("paymentTerms", supplierService.getPaymentTermOptions());

        model.addAttribute("supplierId", supplierId);

        return "admin/supplier-edit";
    }

    @PostMapping("/{supplierId}/update")
    public String update(@PathVariable String supplierId,
            @Valid @ModelAttribute("form") SupplierEditForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("paymentTerms", supplierService.getPaymentTermOptions());
            model.addAttribute("supplierId", supplierId);
            return "admin/supplier-edit";
        }
        supplierService.update(supplierId, form);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.update.success", null, null));
        redirectAttributes.addAttribute("supplierId", supplierId);
        
        return "redirect:/admin/setting/supplier/{supplierId}";
    }

}
