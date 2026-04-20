package com.example.p2p.validation;

import org.springframework.stereotype.Component;

import com.example.p2p.form.app.InitialPasswordSetupForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ComparePasswordValidator implements ConstraintValidator<ComparePassword, InitialPasswordSetupForm> {

    private String message;

    @Override
    public void initialize(ComparePassword constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(InitialPasswordSetupForm value, ConstraintValidatorContext context) {
        if (value.getPassword() == null || value.getConfirmPassword() == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode("confirmPassword")
            .addConstraintViolation();

        return value.getPassword().equals(value.getConfirmPassword());
    }

}
