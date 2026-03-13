package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnitCreateForm {

    @NotBlank
    @Length(max = 50)
    private String name;
}
