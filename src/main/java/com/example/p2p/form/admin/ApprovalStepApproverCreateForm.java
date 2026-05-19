package com.example.p2p.form.admin;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ApprovalStepApproverCreateForm {

    @NotBlank
    private String userId;

    @NotNull
    @PositiveOrZero
    private Integer amountMin;

    @PositiveOrZero
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
