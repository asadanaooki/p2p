package com.example.p2p.dto;

import com.example.p2p.enums.RoleName;
import com.example.p2p.enums.VisibilityScope;

import lombok.Data;

@Data
public class RoleDetailDto {

    private RoleName name;

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

    public boolean isPrFullAccess() {
        return prCreate && prViewScope == VisibilityScope.ALL;
    }

    public boolean isPoFullAccess() {
        return poCreate && poViewScope == VisibilityScope.ALL;
    }

    public boolean isReceiptFullAccess() {
        return receiptCreate && receiptViewScope == VisibilityScope.ALL;
    }

    public boolean isInvoiceFullAccess() {
        return invoiceCreate && invoiceViewScope == VisibilityScope.ALL;
    }

}
