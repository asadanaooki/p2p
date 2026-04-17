package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InitialPasswordSetupForm {
    
    @NotBlank
    private String token;

    @NotBlank
    @Length(min = 8, max = 60)
    @Pattern(regexp = "^[!-~]+$")
    private String password;
    
    @NotBlank
    @Length(min = 8, max = 60)
    @Pattern(regexp = "^[!-~]+$")
    private String confirmPassword;
    
    // TODO: PWの相関関係
}
