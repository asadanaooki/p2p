package com.example.p2p.form.admin;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ApprovalWorkflowCreateForm {
    
    @NotEmpty(message = "{error.approval.steps.required}")
    private List<@Valid ApprovalStepCreateForm> approvalSteps;
}
