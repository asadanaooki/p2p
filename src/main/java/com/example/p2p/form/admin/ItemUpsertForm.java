package com.example.p2p.form.admin;

import org.hibernate.validator.constraints.Length;

import com.example.p2p.enums.ItemKind;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ItemUpsertForm {

    @NotBlank
    @Length(max = 100)
    private String name;

    @NotNull
    private ItemKind kind;

    @Length(min = 36, max = 36)
    private String unitId;

    @PositiveOrZero
    private Integer price;

    @Length(min = 36, max = 36)
    private String supplierId;

    @Length(max = 500)
    private String description;

    private Boolean active;

}
