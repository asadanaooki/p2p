package com.example.p2p.form.admin;

import java.util.List;

import lombok.Data;

@Data
public class ApprovalStepCreateForm {

    private String name;
    
    private List<ApprovalStepApproverCreateForm> approvers;
    
}
