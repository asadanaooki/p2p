package com.example.p2p.dto.admin;

import lombok.Data;

@Data
public class ApprovalStepApproverDto {
    
    private String approvalStepApproverId;

    private String userId;

    private String userName;

    private String email;

    private Integer amountMin;

    private Integer amountMax;

}
