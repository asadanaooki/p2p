package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileEditForm {

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

}
