package com.example.p2p.form.admin;

import lombok.Data;

@Data
public class ApprovalStepApproverCreateForm {

    private String userId;
    
    private Integer amountMin;
    
    private Integer amountMax;
    
}
