package com.example.p2p.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileDto {

    private String lastName;

    private String firstName;

    private String lastNameKana;

    private String firstNameKana;

}
