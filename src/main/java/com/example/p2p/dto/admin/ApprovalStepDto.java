package com.example.p2p.dto.admin;

import java.util.List;

import lombok.Data;

@Data
public class ApprovalStepDto {
    
    private String approvalStepId;

    private String name;
    
    private int stepOrder;
    
    private List<ApprovalStepApproverDto> approvalStepApprovers;
}
