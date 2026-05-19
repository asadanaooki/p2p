package com.example.p2p.form.admin;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ApprovalStepApproverCreateForm {

    @NotBlank(message = "{error.approval.approver.user.required}")
    private String userId;

    @NotNull(message = "{error.approval.approver.amount.min.required}")
    @PositiveOrZero(message = "{error.approval.approver.amount.min.positiveOrZero}")
    private Integer amountMin;

    @PositiveOrZero(message = "{error.approval.approver.amount.max.positiveOrZero}")
    private Integer amountMax;

    // 表示用
    private String userName;

    private String email;

    @AssertTrue(message = "{error.approval.amount.range.invalid}")
    public boolean isAmountRangeValid() {
        if (amountMin == null || amountMax == null) {
            return true;
        }
        return amountMin <= amountMax;
    }

}
