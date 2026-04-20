package com.example.p2p.form.admin;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierCreateForm {

    @NotBlank
    @Length(max = 50)
    private String name;

    @Email
    @Length(max = 254)
    private String email;

    @Length(max = 11)
    private String phoneNumber;

    @Length(min = 7,max = 7)
    private String postalCode;

    @Length(max = 5)
    private String prefecture;

    @Length(max = 50)
    private String city;

    @Length(max = 50)
    private String streetAddress;

    @Length(max = 50)
    private String buildingName;

    @Length(min = 36, max = 36)
    private String paymentTermId;
}