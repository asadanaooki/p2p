package com.example.p2p.dto;

import lombok.Data;

@Data
public class UserDetailDto {

    private String lastName;

    private String firstName;

    private String lastNameKana;

    private String firstNameKana;

    private String email;
    
    private String roleId;

    private String roleName;

    private Boolean isActive;
    
    private boolean canInvite;

}
