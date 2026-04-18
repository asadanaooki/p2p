package com.example.p2p.validation;

import org.springframework.stereotype.Component;

import com.example.p2p.form.InitialPasswordSetupForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ComparePasswordValidator
        implements ConstraintValidator<RolePermissionCombination, InitialPasswordSetupForm> {

    @Override
    public boolean isValid(InitialPasswordSetupForm value, ConstraintValidatorContext context) {
        if (value.getPassword() == null || value.getConfirmPassword() == null) {
            return true;
        }
        return value.getPassword().equals(value.getConfirmPassword());
    }

}
