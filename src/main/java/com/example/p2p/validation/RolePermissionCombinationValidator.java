package com.example.p2p.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.admin.RoleUpsertForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RolePermissionCombinationValidator
        implements ConstraintValidator<RolePermissionCombination, RoleUpsertForm> {

    private MessageSource messageSource;

    @Override
    public boolean isValid(RoleUpsertForm value, ConstraintValidatorContext context) {
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

        context.disableDefaultConstraintViolation();

        boolean isValid = true;
        // PR
        isValid &= isPermissionValid(
                value.isPrCreate(),
                value.isPrApprove(),
                value.getPrViewScope(),
                context,
                "role.pr.create.viewScope.error",
                "role.pr.approve.viewScope.error");
        // PO
        isValid &= isPermissionValid(
                value.isPoCreate(),
                value.isPoApprove(),
                value.getPoViewScope(),
                context,
                "role.po.create.viewScope.error",
                "role.po.approve.viewScope.error");
        // Receipt
        isValid &= isReceiptPermissionValid(
                value,
                context,
                "role.receipt.create.viewScope.error");
        // Invoice
        isValid &= isPermissionValid(
                value.isInvoiceCreate(),
                value.isInvoiceApprove(),
                value.getInvoiceViewScope(),
                context,
                "role.invoice.create.viewScope.error",
                "role.invoice.approve.viewScope.error");

        return isValid;
    }

    private boolean isPermissionValid(boolean isCreate, boolean isApprove, VisibilityScope scope,
            ConstraintValidatorContext context, String createViolationMessageKey, String approveViolationMessageKey) {
        boolean isValid = true;
        if (isApprove && scope != VisibilityScope.ALL) {
            context
                .buildConstraintViolationWithTemplate(messageSource.getMessage(approveViolationMessageKey, null, null))
                .addConstraintViolation();
            isValid = false;
        }
        if (isCreate && !isApprove && scope != VisibilityScope.ALL && scope != VisibilityScope.SELF) {
            context
                .buildConstraintViolationWithTemplate(messageSource.getMessage(createViolationMessageKey, null, null))
                .addConstraintViolation();
            isValid = false;
        }
        return isValid;
    }

    private boolean isReceiptPermissionValid(
            RoleUpsertForm form,
            ConstraintValidatorContext context,
            String createViolationMessageKey) {
        if (form.isReceiptCreate() && form.getReceiptViewScope() != VisibilityScope.ALL
                && form.getReceiptViewScope() != VisibilityScope.SELF) {
            context
                .buildConstraintViolationWithTemplate(messageSource.getMessage(createViolationMessageKey, null, null))
                .addConstraintViolation();
            return false;
        }
        return true;
    }

}
