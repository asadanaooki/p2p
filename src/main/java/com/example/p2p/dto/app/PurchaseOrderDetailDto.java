package com.example.p2p.dto.app;

import java.time.LocalDate;
import java.util.List;

import com.example.p2p.enums.ApprovalStatus;
import com.example.p2p.enums.PurchaseOrderStatus;
import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.util.CommonUtil;

import lombok.Data;

@Data
public class PurchaseOrderDetailDto {

    // Mybatis用
    private String poId;

    // ヘッダー
    private Integer displayNumber;

    private PurchaseOrderType orderType;

    private String purchaser;

    private String supplierName;

    private int totalAmountExcludingTax;

    private PurchaseOrderStatus status;

    private String note;

    private LocalDate createdAt;

    private String userId;

    // POのみ
    private LocalDate deliveryDueDate;

    // SOのみ
    private LocalDate servicePeriodFrom;

    private LocalDate servicePeriodTo;

    // 関連するPR
    private List<PurchaseOrderRelatedPrDto> relatedPrs;

    // 明細
    private List<PurchaseOrderDetailLineDto> details;

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
        return status == PurchaseOrderStatus.PENDING || status == PurchaseOrderStatus.APPROVED
                || status == PurchaseOrderStatus.REJECTED;
    }

}