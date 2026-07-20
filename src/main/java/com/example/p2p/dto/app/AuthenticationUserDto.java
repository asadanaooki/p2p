package com.example.p2p.dto.app;

import com.example.p2p.enums.VisibilityScope;

import lombok.Data;

@Data
public class AuthenticationUserDto {

    private String userId;

    private String fullName;
    
    private String passwordHash;
    
    private String email;
    
    private String roleName;
    
    private Boolean prCreate;

    private Boolean poCreate;

    private Boolean receiptCreate;

    private Boolean invoiceCreate;

    private VisibilityScope prViewScope;

    private VisibilityScope poViewScope;

    private VisibilityScope receiptViewScope;

    private VisibilityScope invoiceViewScope;

    private Boolean prApprove;

    private Boolean poApprove;

    private Boolean invoiceApprove;

    private Boolean settingManage;
}
