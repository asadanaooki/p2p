package com.example.p2p.dto.app;

import java.time.LocalDate;
import java.util.List;

import com.example.p2p.enums.ApprovalStatus;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.util.CommonUtil;

import lombok.Data;

@Data
public class PurchaseRequestDetailDto {

    // Mybatis用
    private String prId;

    // ヘッダー
    private Integer displayNumber;

    private String requester;

    private LocalDate dueDate;

    private int totalAmountExcludingTax;

    private PurchaseRequestStatus status;

    private String note;

    private LocalDate createdAt;

    private String userId;

    private List<PurchaseRequestRelatedPoDto> relatedPos;

    // 明細
    private List<PurchaseRequestDetailLineDto> details;

    // 承認状況
    private List<ApprovalProgressStepDto> approvalProgressSteps;

    private int currentStepOrder;

    // 承認/否認ボタン表示判定
    public boolean isApprovalActionButtonVisible() {
        return CommonUtil.isApprovalActionButtonVisible(status.toString(), approvalProgressSteps, currentStepOrder,
                userId);
    }

    public ApprovalStatus getCurrentApprovalStepStatus() {
        return CommonUtil.getCurrentApprovalStepStatus(approvalProgressSteps, currentStepOrder);
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
