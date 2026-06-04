package com.example.p2p.form.admin;

import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.springframework.util.CollectionUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ApprovalStepCreateForm {

    // HTMLエラー防止用
    private String approvalStepId;

    @NotBlank(message = "{error.approval.step.name.required}")
    @Length(max = 100, message = "{error.approval.step.name.length}")
    private String name;

    @NotEmpty(message = "{error.approval.step.approvers.required}")
    private List<@Valid ApprovalStepApproverCreateForm> approvalStepApprovers;

    @AssertTrue(message = "{error.approval.step.approver.duplicate}")
    public boolean isApproverUniqueInStep() {
        if (CollectionUtils.isEmpty(approvalStepApprovers)) {
            return true;
        }
        Set<String> userIds = new HashSet<String>();
        for (ApprovalStepApproverCreateForm approver : approvalStepApprovers) {
            if (!userIds.add(approver.getUserId())) {
                return false;
            }
        }
        return true;
    }

}
