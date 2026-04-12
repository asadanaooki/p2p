package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpsertForm {

    @NotBlank
    @Length(max = 50)
    private String lastName;
    
    @NotBlank
    @Length(max = 50)
    private String firstName;
    
    @NotBlank
    @Length(max = 50)
    private String lastNameKana;
    
    @NotBlank
    @Length(max = 50)
    private String firstNameKana;
    
    @NotBlank
    @Email
    @Length(max = 254)
    private String email;
    
    @NotBlank
    @Length(max = 255)
    @Pattern(regexp = "^[!-~]+$")
    private String password;

    @NotNull
    @Length(min = 36, max = 36)
    private String roleId;

    private Boolean active;

}
