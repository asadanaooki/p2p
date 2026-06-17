package com.example.p2p.dto.app;

import java.time.LocalDate;
import java.util.List;

import com.example.p2p.enums.ApprovalStatus;
import com.example.p2p.enums.PurchaseRequestStatus;

import lombok.Data;

@Data
public class PurchaseRequestDetailDto {

    // ヘッダー
    private Integer displayNumber;

    private String requester;

    private LocalDate dueDate;

    private int totalAmountExcludingTax;

    private PurchaseRequestStatus status;

    private String note;

    private LocalDate createdAt;

    private String userId;

    // 明細
    private List<PurchaseRequestDetailLineDto> details;

    // 承認状況
    private List<ApprovalProgressStepDto> approvalProgressSteps;

    private int currentStepOrder;

    // 承認/否認ボタン表示判定
    public boolean isApprovalActionButtonVisible() {
        if (status != PurchaseRequestStatus.PENDING) {
            return false;
        }
        return approvalProgressSteps.stream()
            .filter(s -> s.getStepOrder() == currentStepOrder)
            .flatMap(s -> s.getApprovalProgressApprovers().stream())
            .anyMatch(a -> a.getUserId().equals(userId) && a.getStatus() == ApprovalStatus.PENDING);
    }

    public ApprovalStatus getCurrentApprovalStepStatus() {
        boolean rejected = approvalProgressSteps.stream()
            .filter(s -> s.getStepOrder() == currentStepOrder)
            .flatMap(s -> s.getApprovalProgressApprovers().stream())
            .anyMatch(a -> a.getStatus() == ApprovalStatus.REJECTED);

        return rejected ? ApprovalStatus.REJECTED : ApprovalStatus.PENDING;
    }

    public int getTaxAmount() {
        return totalAmountExcludingTax * 10 / 100;
    }

    public int getTotalAmountIncludingTax() {
        return totalAmountExcludingTax + getTaxAmount();
    }

    public boolean isCanEdit() {
        return status == PurchaseRequestStatus.PENDING || status == PurchaseRequestStatus.APPROVED
                || status == PurchaseRequestStatus.REJECTED;
    }

}