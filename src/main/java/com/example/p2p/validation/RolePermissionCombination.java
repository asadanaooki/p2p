package com.example.p2p.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = RolePermissionCombinationValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RolePermissionCombination {
    String message() default "permission combination error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
