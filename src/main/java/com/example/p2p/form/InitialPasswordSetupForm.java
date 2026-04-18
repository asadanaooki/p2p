package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.validation.ComparePassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InitialPasswordSetupForm {

    @NotBlank
    private String token;

    @NotBlank
    @Length(min = 8, max = 60, message = "{error.length.range}")
    @Pattern(regexp = "^[!-~]+$", message = "{error.halfwidth.alnum.symbol}")
    private String password;

    @NotBlank
    @Length(min = 8, max = 60, message = "{error.length.range}")
    @Pattern(regexp = "^[!-~]+$", message = "{error.halfwidth.alnum.symbol}")
    @ComparePassword(message = "{error.password.mismatch}")
    private String confirmPassword;

}
