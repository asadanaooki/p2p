package com.example.p2p.dto;

import lombok.Data;

@Data
public class SupplierDetailDto {

    private String supplierName;

    private String email;

    private String phoneNumber;

    private String postalCode;

    private String prefecture;

    private String city;

    private String streetAddress;

    private String buildingName;
    
    private String paymentTermId;

    private String paymentTermName;

    private boolean active;

}