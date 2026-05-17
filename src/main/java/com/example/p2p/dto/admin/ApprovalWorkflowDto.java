package com.example.p2p.dto.admin;

import java.util.List;

import lombok.Data;

@Data
public class ApprovalWorkflowDto {

    private String approvalWorkflowId;

    List<ApprovalStepDto> approvalSteps;

}
