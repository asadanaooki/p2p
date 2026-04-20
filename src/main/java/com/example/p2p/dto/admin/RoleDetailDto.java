package com.example.p2p.dto.admin;

import com.example.p2p.enums.VisibilityScope;

import lombok.Data;

@Data
public class RoleDetailDto {

    private String name;

    private boolean prCreate;

    private boolean poCreate;

    private boolean receiptCreate;

    private boolean invoiceCreate;

    private VisibilityScope prViewScope;

    private VisibilityScope poViewScope;

    private VisibilityScope receiptViewScope;

    private VisibilityScope invoiceViewScope;

    private boolean prApprove;

    private boolean poApprove;

    private boolean invoiceApprove;

    private boolean settingManage;

}
