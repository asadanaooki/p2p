package com.example.p2p.form;

import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.validation.RolePermissionCombination;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@RolePermissionCombination
public class RoleEditForm {

    @NotNull
    private String name;

    private boolean prCreate;

    private boolean poCreate;

    private boolean receiptCreate;

    private boolean invoiceCreate;

    @NotNull
    private VisibilityScope prViewScope;

    @NotNull
    private VisibilityScope poViewScope;

    @NotNull
    private VisibilityScope receiptViewScope;

    @NotNull
    private VisibilityScope invoiceViewScope;

    private boolean prApprove;

    private boolean poApprove;

    private boolean invoiceApprove;

    private boolean settingManage;

}
