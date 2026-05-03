package com.example.p2p.validation;

import com.example.p2p.enums.DetailInputType;
import com.example.p2p.form.app.PurchaseRequestDetailForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPurchaseRequestDetailValidator
        implements ConstraintValidator<ValidPurchaseRequestDetail, PurchaseRequestDetailForm> {

    @Override
    public boolean isValid(PurchaseRequestDetailForm value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.getDetailInputType() == null) {
            return false;
        }

        if (value.getQuantity() == null || value.getQuantity() < 1) {
            return false;
        }

        if (value.getDetailInputType() == DetailInputType.CATALOG) {
            return value.getItemId() != null
                    && value.getItemId().length() == 36;
        }

        if (value.getDetailInputType() == DetailInputType.FREE) {
            return value.getKind() != null
                    && value.getItemName() != null
                    && value.getItemName().length() <= 100
                    && (value.getSupplierId() == null || value.getSupplierId().length() == 36)
                    && value.getSupplierName() != null
                    && value.getSupplierName().length() <= 100
                    && (value.getUnitId() == null || value.getUnitId().length() == 36)
                    && value.getUnitName() != null
                    && value.getUnitName().length() <= 50
                    && value.getPrice() != null
                    && value.getPrice() >= 0;
        }

        return false;
    }
}