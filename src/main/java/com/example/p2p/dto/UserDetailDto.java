package com.example.p2p.dto;

import lombok.Data;

@Data
public class UserDetailDto {

    private String userId;

    private String lastName;

    private String firstName;

    private String lastNameKana;

    private String firstNameKana;

    private String email;

    private String roleName;

    private boolean isActive;

}
