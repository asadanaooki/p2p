package com.example.p2p.form.admin;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ApprovalStepCreateForm {
    
    // HTMLエラー防止用
    private String approvalStepId;

    @NotBlank
    @Length(max = 100)
    private String name;
    
    @NotEmpty
    private List<@Valid ApprovalStepApproverCreateForm> approvalStepApprovers;
    
}
