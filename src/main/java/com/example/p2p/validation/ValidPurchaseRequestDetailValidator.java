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
            addViolation(context, "nonForm", "error.purchaseRequest.detail.invalid");
            return false;
        }

        if (value.getDetailInputType() == null) {
            addViolation(context, "detailInputType", "error.purchaseRequest.detail.invalid");
            return false;
        }

        boolean isValid = true;

        if (value.getQuantity() == null || value.getQuantity() < 1) {
            addViolation(context, "quantity", "error.purchaseRequest.detail.quantity.requiredPositive");
            isValid = false;
        }

        if (value.getDetailInputType() == DetailInputType.CATALOG) {
            if (value.getItemId() == null || value.getItemId().length() != 36) {
                addViolation(context, "itemId", "error.purchaseRequest.detail.itemId.requiredLength");
                isValid = false;
            }
        }

        if (value.getDetailInputType() == DetailInputType.FREE) {
            if (value.getKind() == null) {
                addViolation(context, "kind", "error.purchaseRequest.detail.kind.required");
                isValid = false;
            }

            if (value.getItemName() == null || value.getItemName().length() > 100) {
                addViolation(context, "itemName", "error.purchaseRequest.detail.itemName.requiredMax");
                isValid = false;
            }

            if (value.getSupplierId() != null && value.getSupplierId().length() != 36) {
                addViolation(context, "supplierId", "error.purchaseRequest.detail.supplierId.invalid");
                isValid = false;
            }

            if (value.getSupplierName() == null || value.getSupplierName().length() > 100) {
                addViolation(context, "supplierName", "error.purchaseRequest.detail.supplierName.requiredMax");
                isValid = false;
            }

            if (value.getUnitId() != null && value.getUnitId().length() != 36) {
                addViolation(context, "unitId", "error.purchaseRequest.detail.unitId.invalid");
                isValid = false;
            }

            if (value.getUnitName() == null || value.getUnitName().length() > 50) {
                addViolation(context, "unitName", "error.purchaseRequest.detail.unitName.requiredMax");
                isValid = false;
            }

            if (value.getPrice() == null || value.getPrice() < 0) {
                addViolation(context, "price", "error.purchaseRequest.detail.price.requiredPositiveOrZero");
                isValid = false;
            }
        }

        return isValid;
    }

    private void addViolation(ConstraintValidatorContext context, String field, String messageKey) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("{" + messageKey + "}")
            .addPropertyNode(field)
            .addConstraintViolation();
    }

}