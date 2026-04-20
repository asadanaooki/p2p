package com.example.p2p.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.admin.RoleUpsertForm;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest
class RolePermissionCombinationValidatorTest {

    @Autowired
    Validator validator;
    
    @Autowired
    MessageSource messageSource;

    @ParameterizedTest
    @MethodSource("createPrPermissionCases")
    void rolePermissionCombinationValidator_pr(boolean isCreate, boolean isApprove, VisibilityScope scope,
            boolean expIsValid, String expMessageKey) {
        RoleUpsertForm form = baseForm();
        form.setPrCreate(isCreate);
        form.setPrApprove(isApprove);
        form.setPrViewScope(scope);

        Set<ConstraintViolation<RoleUpsertForm>> result = validator.validate(form);
        
            assertThat(result.isEmpty()).isEqualTo(expIsValid);
            if (!result.isEmpty()) {
                assertThat(result).hasSize(1);
               assertThat(result.iterator().next().getMessage())
               .isEqualTo(messageSource.getMessage(expMessageKey, null, null));
            }

        
    }

    @ParameterizedTest
    @MethodSource("createPoPermissionCases")
    void rolePermissionCombinationValidator_po(boolean isCreate, boolean isApprove, VisibilityScope scope,
            boolean expIsValid, String expMessageKey) {
        RoleUpsertForm form = baseForm();
        form.setPoCreate(isCreate);
        form.setPoApprove(isApprove);
        form.setPoViewScope(scope);

        Set<ConstraintViolation<RoleUpsertForm>> result = validator.validate(form);
        
        assertThat(result.isEmpty()).isEqualTo(expIsValid);
        if (!result.isEmpty()) {
            assertThat(result).hasSize(1);
           assertThat(result.iterator().next().getMessage())
           .isEqualTo(messageSource.getMessage(expMessageKey, null, null));
        }
    }

    @ParameterizedTest
    @MethodSource("createReceiptPermissionCases")
    void rolePermissionCombinationValidator_receipt(boolean isCreate, VisibilityScope scope,
            boolean expIsValid, String expMessageKey) {
        RoleUpsertForm form = baseForm();
        form.setReceiptCreate(isCreate);
        form.setReceiptViewScope(scope);

        Set<ConstraintViolation<RoleUpsertForm>> result = validator.validate(form);

        assertThat(result.isEmpty()).isEqualTo(expIsValid);
        if (!result.isEmpty()) {
            assertThat(result).hasSize(1);
           assertThat(result.iterator().next().getMessage())
           .isEqualTo(messageSource.getMessage(expMessageKey, null, null));
        }
    }

    @ParameterizedTest
    @MethodSource("createInvoicePermissionCases")
    void rolePermissionCombinationValidator_invoice(boolean isCreate, boolean isApprove, VisibilityScope scope,
            boolean expIsValid, String expMessageKey) {
        RoleUpsertForm form = baseForm();
        form.setInvoiceCreate(isCreate);
        form.setInvoiceApprove(isApprove);
        form.setInvoiceViewScope(scope);

        Set<ConstraintViolation<RoleUpsertForm>> result = validator.validate(form);

        assertThat(result.isEmpty()).isEqualTo(expIsValid);
        if (!result.isEmpty()) {
            assertThat(result).hasSize(1);
           assertThat(result.iterator().next().getMessage())
           .isEqualTo(messageSource.getMessage(expMessageKey, null, null));
        }
    }

    private static Stream<Arguments> createPrPermissionCases() {
        return Stream.of(
                Arguments.of(true,  true,  VisibilityScope.ALL,  true,  null),
                Arguments.of(true,  true,  VisibilityScope.SELF, false, "role.pr.approve.viewScope.error"),
                Arguments.of(true,  true,  VisibilityScope.NONE, false, "role.pr.approve.viewScope.error"),
                Arguments.of(true,  false, VisibilityScope.ALL,  true,  null),
                Arguments.of(true,  false, VisibilityScope.SELF, true,  null),
                Arguments.of(true,  false, VisibilityScope.NONE, false, "role.pr.create.viewScope.error"),
                Arguments.of(false, true,  VisibilityScope.ALL,  true,  null),
                Arguments.of(false, true,  VisibilityScope.SELF, false, "role.pr.approve.viewScope.error"),
                Arguments.of(false, true,  VisibilityScope.NONE, false, "role.pr.approve.viewScope.error"),
                Arguments.of(false, false, VisibilityScope.ALL,  true,  null),
                Arguments.of(false, false, VisibilityScope.SELF, true,  null),
                Arguments.of(false, false, VisibilityScope.NONE, true,  null)
        );
    }

    private static Stream<Arguments> createPoPermissionCases() {
        return Stream.of(
                Arguments.of(true,  true,  VisibilityScope.ALL,  true,  null),
                Arguments.of(true,  true,  VisibilityScope.SELF, false, "role.po.approve.viewScope.error"),
                Arguments.of(true,  true,  VisibilityScope.NONE, false, "role.po.approve.viewScope.error"),
                Arguments.of(true,  false, VisibilityScope.ALL,  true,  null),
                Arguments.of(true,  false, VisibilityScope.SELF, true,  null),
                Arguments.of(true,  false, VisibilityScope.NONE, false, "role.po.create.viewScope.error"),
                Arguments.of(false, true,  VisibilityScope.ALL,  true,  null),
                Arguments.of(false, true,  VisibilityScope.SELF, false, "role.po.approve.viewScope.error"),
                Arguments.of(false, true,  VisibilityScope.NONE, false, "role.po.approve.viewScope.error"),
                Arguments.of(false, false, VisibilityScope.ALL,  true,  null),
                Arguments.of(false, false, VisibilityScope.SELF, true,  null),
                Arguments.of(false, false, VisibilityScope.NONE, true,  null)
        );
    }

    private static Stream<Arguments> createReceiptPermissionCases() {
        return Stream.of(
                Arguments.of(true,  VisibilityScope.ALL,  true,  null),
                Arguments.of(true,  VisibilityScope.SELF, true,  null),
                Arguments.of(true,  VisibilityScope.NONE, false, "role.receipt.create.viewScope.error"),
                Arguments.of(false, VisibilityScope.ALL,  true,  null),
                Arguments.of(false, VisibilityScope.SELF, true,  null),
                Arguments.of(false, VisibilityScope.NONE, true,  null)
        );
    }

    private static Stream<Arguments> createInvoicePermissionCases() {
        return Stream.of(
                Arguments.of(true,  true,  VisibilityScope.ALL,  true,  null),
                Arguments.of(true,  true,  VisibilityScope.SELF, false, "role.invoice.approve.viewScope.error"),
                Arguments.of(true,  true,  VisibilityScope.NONE, false, "role.invoice.approve.viewScope.error"),
                Arguments.of(true,  false, VisibilityScope.ALL,  true,  null),
                Arguments.of(true,  false, VisibilityScope.SELF, true,  null),
                Arguments.of(true,  false, VisibilityScope.NONE, false, "role.invoice.create.viewScope.error"),
                Arguments.of(false, true,  VisibilityScope.ALL,  true,  null),
                Arguments.of(false, true,  VisibilityScope.SELF, false, "role.invoice.approve.viewScope.error"),
                Arguments.of(false, true,  VisibilityScope.NONE, false, "role.invoice.approve.viewScope.error"),
                Arguments.of(false, false, VisibilityScope.ALL,  true,  null),
                Arguments.of(false, false, VisibilityScope.SELF, true,  null),
                Arguments.of(false, false, VisibilityScope.NONE, true,  null)
        );
    }
    
    private RoleUpsertForm baseForm() {
        RoleUpsertForm form = new RoleUpsertForm();
        form.setName("管理者");
        form.setPrCreate(true);
        form.setPoCreate(true);
        form.setReceiptCreate(true);
        form.setInvoiceCreate(true);
        form.setPrViewScope(VisibilityScope.ALL);
        form.setPoViewScope(VisibilityScope.ALL);
        form.setReceiptViewScope(VisibilityScope.ALL);
        form.setInvoiceViewScope(VisibilityScope.ALL);
        form.setPrApprove(true);
        form.setPoApprove(true);
        form.setInvoiceApprove(true);
        form.setSettingManage(true);

        return form;
    }

}
