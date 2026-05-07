package com.example.p2p.form.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.example.p2p.enums.ItemKind;

@SpringBootTest
class PurchaseRequestCreateFormTest {

    @Autowired
    Validator validator;
    
    @Test void validPurchaseRequestCreateForm_fail() {
        PurchaseRequestDetailForm form = new PurchaseRequestDetailForm();
        form.setDetailInputType(null);
        form.setItemId(UUID.randomUUID().toString());
        form.setKind(ItemKind.GOODS);
        form.setItemName("testItem");
        form.setSupplierId(UUID.randomUUID().toString());
        form.setSupplierName("testSupplier");
        form.setUnitId(UUID.randomUUID().toString());
        form.setUnitName("testSupplier");
        form.setPrice(1500);
        form.setQuantity(3);
        
        PurchaseRequestCreateForm createForm = new PurchaseRequestCreateForm();
        createForm.setDetails(List.of(form));
        
        BindingResult bindingResult = new BindException(createForm, "purchaseRequestCreateForm");
        validator.validate(createForm, bindingResult);
        
        
        assertThat(bindingResult.hasErrors()).isTrue();
    }
}
