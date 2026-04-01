package com.example.p2p.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.RoleEditForm;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class RolePermissionCombinationValidatorTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @MethodSource("createPrPermissionCases")
    void rolePermissionCombinationValidator_pr(boolean isCreate, boolean isApprove, VisibilityScope scope,
            boolean expected) {
        RoleEditForm form = baseForm();
        form.setPrCreate(isCreate);
        form.setPrApprove(isApprove);
        form.setPrViewScope(scope);

        Set<ConstraintViolation<RoleEditForm>> result = validator.validate(form);

        assertThat(result.size() == 0 ? true : false).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("createPoPermissionCases")
    void rolePermissionCombinationValidator_po(boolean isCreate, boolean isApprove, VisibilityScope scope,
            boolean expected) {
        RoleEditForm form = baseForm();
        form.setPoCreate(isCreate);
        form.setPoApprove(isApprove);
        form.setPoViewScope(scope);

        Set<ConstraintViolation<RoleEditForm>> result = validator.validate(form);

        assertThat(result.size() == 0 ? true : false).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("createReceiptPermissionCases")
    void rolePermissionCombinationValidator_receipt(boolean isCreate, VisibilityScope scope, boolean expected) {
        RoleEditForm form = baseForm();
        form.setReceiptCreate(isCreate);
        form.setReceiptViewScope(scope);

        Set<ConstraintViolation<RoleEditForm>> result = validator.validate(form);

        assertThat(result.size() == 0 ? true : false).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("createInvoicePermissionCases")
    void rolePermissionCombinationValidator_invoice(boolean isCreate, boolean isApprove, VisibilityScope scope,
            boolean expected) {
        RoleEditForm form = baseForm();
        form.setInvoiceCreate(isCreate);
        form.setInvoiceApprove(isApprove);
        form.setInvoiceViewScope(scope);

        Set<ConstraintViolation<RoleEditForm>> result = validator.validate(form);

        assertThat(result.size() == 0 ? true : false).isEqualTo(expected);
    }

    private static Stream<Arguments> createPrPermissionCases() {
        return Stream.of(Arguments.of(true, true, VisibilityScope.ALL, true),
                Arguments.of(true, true, VisibilityScope.SELF, false),
                Arguments.of(true, true, VisibilityScope.NONE, false),
                Arguments.of(true, false, VisibilityScope.ALL, true),
                Arguments.of(true, false, VisibilityScope.SELF, true),
                Arguments.of(true, false, VisibilityScope.NONE, false),
                Arguments.of(false, true, VisibilityScope.ALL, true),
                Arguments.of(false, true, VisibilityScope.SELF, false),
                Arguments.of(false, true, VisibilityScope.NONE, false),
                Arguments.of(false, false, VisibilityScope.ALL, true),
                Arguments.of(false, false, VisibilityScope.SELF, true),
                Arguments.of(false, false, VisibilityScope.NONE, true));
    }

    private static Stream<Arguments> createPoPermissionCases() {
        return Stream.of(Arguments.of(true, true, VisibilityScope.ALL, true),
                Arguments.of(true, true, VisibilityScope.SELF, false),
                Arguments.of(true, true, VisibilityScope.NONE, false),
                Arguments.of(true, false, VisibilityScope.ALL, true),
                Arguments.of(true, false, VisibilityScope.SELF, true),
                Arguments.of(true, false, VisibilityScope.NONE, false),
                Arguments.of(false, true, VisibilityScope.ALL, true),
                Arguments.of(false, true, VisibilityScope.SELF, false),
                Arguments.of(false, true, VisibilityScope.NONE, false),
                Arguments.of(false, false, VisibilityScope.ALL, true),
                Arguments.of(false, false, VisibilityScope.SELF, true),
                Arguments.of(false, false, VisibilityScope.NONE, true));
    }

    private static Stream<Arguments> createReceiptPermissionCases() {
        return Stream.of(Arguments.of(true, VisibilityScope.ALL, true), Arguments.of(true, VisibilityScope.SELF, true),
                Arguments.of(true, VisibilityScope.NONE, false), Arguments.of(false, VisibilityScope.ALL, true),
                Arguments.of(false, VisibilityScope.SELF, true), Arguments.of(false, VisibilityScope.NONE, true));
    }

    private static Stream<Arguments> createInvoicePermissionCases() {
        return Stream.of(Arguments.of(true, true, VisibilityScope.ALL, true),
                Arguments.of(true, true, VisibilityScope.SELF, false),
                Arguments.of(true, true, VisibilityScope.NONE, false),
                Arguments.of(true, false, VisibilityScope.ALL, true),
                Arguments.of(true, false, VisibilityScope.SELF, true),
                Arguments.of(true, false, VisibilityScope.NONE, false),
                Arguments.of(false, true, VisibilityScope.ALL, true),
                Arguments.of(false, true, VisibilityScope.SELF, false),
                Arguments.of(false, true, VisibilityScope.NONE, false),
                Arguments.of(false, false, VisibilityScope.ALL, true),
                Arguments.of(false, false, VisibilityScope.SELF, true),
                Arguments.of(false, false, VisibilityScope.NONE, true));
    }

    private RoleEditForm baseForm() {
        RoleEditForm form = new RoleEditForm();
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
