package com.example.p2p.dto.admin;

import lombok.Data;

@Data
public class UserListRowDto {
    
    private String userId;

    private String lastName;
    
    private String firstName;
    
    private String lastNameKana;
    
    private String firstNameKana;
    
    private String email;
    
    private String roleId;
    
    private String roleName;
    
    private boolean isActive;
}
