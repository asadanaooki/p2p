package com.example.p2p.form.app;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailChangeForm {

    @NotBlank 
    @Length(max = 254) 
    @Email
    private String newEmail;

}
