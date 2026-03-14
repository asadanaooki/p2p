package com.example.p2p.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitEditForm {

    @NotBlank
    @Length(max = 50)
    private String name;

    private boolean status;

}
