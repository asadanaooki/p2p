package com.example.p2p.validation;

import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.RoleEditForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RolePermissionCombinationValidator
        implements ConstraintValidator<RolePermissionCombination, RoleEditForm> {

    @Override
    public boolean isValid(RoleEditForm value, ConstraintValidatorContext context) {
        // PR承認ON→PR閲覧は ALL
        // PR作成ON ＆ PR承認OFF→PR閲覧はSELF OR ALL
        // PR作成OFF ＆ PR承認OFF→PR閲覧は 任意
        //
        //
        // PO承認ON→PO閲覧は ALL
        // PO作成ON ＆ PO承認OFF→PO閲覧はSELF OR ALL
        // PO作成OFF ＆ PO承認OFF→PO閲覧は 任意
        //
        // REC作成ON→REC閲覧はSELF OR ALL
        // REC作成OFF→REC閲覧は 任意
        //
        // INV承認ON→INV閲覧は ALL
        // INV作成ON ＆ INV承認OFF→INV閲覧はSELF OR ALL
        // INV作成OFF ＆ INV承認OFF→INV閲覧は 任意

        return isPermissionValid(value.isPrCreate(), value.isPrApprove(), value.getPrViewScope())
                && isPermissionValid(value.isPoCreate(), value.isPoApprove(), value.getPoViewScope())
                && isReceiptPermissionValid(value)
                && isPermissionValid(value.isInvoiceCreate(), value.isInvoiceApprove(), value.getInvoiceViewScope());

    }

    private boolean isPermissionValid( boolean isPrCreate, boolean isPrApprove, VisibilityScope scope) {
        if (isPrApprove) {
            return scope == VisibilityScope.ALL;
        }
        if (isPrCreate) {
            return (scope == VisibilityScope.ALL || scope == VisibilityScope.SELF);
        }
        return true;
    }

    private boolean isReceiptPermissionValid(RoleEditForm form) {
        if (form.isReceiptCreate()) {
            return (form.getReceiptViewScope() == VisibilityScope.ALL
                    || form.getReceiptViewScope() == VisibilityScope.SELF);
        }
        return true;
    }

}
