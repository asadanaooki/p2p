package com.example.p2p.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class ApprovalProgressStepDto {
    
    private int stepOrder;

    private String stepName;

    List<ApprovalProgressApproverDto> approvalProgressApprovers;

}
