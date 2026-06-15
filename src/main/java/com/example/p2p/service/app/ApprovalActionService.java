package com.example.p2p.service.app;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.ApprovalTask;
import com.example.p2p.entity.ApprovalTaskExample;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.enums.ApprovalStatus;
import com.example.p2p.enums.BusinessErrorCode;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.mapper.ApprovalTaskMapper;
import com.example.p2p.mapper.PurchaseRequestMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ApprovalActionService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalActionService.class);

    private PurchaseRequestMapper purchaseRequestMapper;

    private ApprovalTaskMapper approvalTaskMapper;

    @Transactional
    public void approvePR(String prId, String userId) {
        logger.info("PR承認処理開始");

        PurchaseRequest pr = purchaseRequestMapper.selectByPrimaryKey(prId);
        if (pr.getStatus() != PurchaseRequestStatus.PENDING) {
            logger.warn("PR承認不可");
            throw new BusinessException(BusinessErrorCode.PURCHASE_REQUEST_NOT_PENDING);
        }
        // ドキュメントの現在の承認ステップ
        int currentStep = pr.getCurrentStepOrder();

        ApprovalTaskExample ex = new ApprovalTaskExample();
        ex.createCriteria().andDocumentIdEqualTo(prId);
        List<ApprovalTask> tasks = approvalTaskMapper.selectByExample(ex);

        // 承認者の承認ステップ
        ApprovalTask approverTask = tasks.stream().filter(t -> t.getUserId().equals(userId)).findFirst().orElseThrow();
        if (approverTask.getStatus() == ApprovalStatus.APPROVED) {
            logger.warn("PR承認済みタスクの再承認");
            throw new BusinessException(BusinessErrorCode.APPROVAL_ALREADY_PROCESSED);
        }
        if (approverTask.getStepOrder() != currentStep) {
            logger.warn("PR承認ステップ不一致");
            throw new BusinessException(BusinessErrorCode.APPROVAL_NOT_CURRENT_STEP);
        }
        // 以下承認者の承認ステップ＝ドキュメントの現在の承認ステップ
        // 承認状況と承認処理日時更新
        ApprovalTask updateTask = new ApprovalTask();
        updateTask.setDocumentId(prId);
        updateTask.setStepOrder(approverTask.getStepOrder());
        updateTask.setUserId(approverTask.getUserId());
        updateTask.setStatus(ApprovalStatus.APPROVED);
        updateTask.setActedAt(LocalDateTime.now());
        approvalTaskMapper.updateByPrimaryKeySelective(updateTask);

        // 最新の承認タスクを再取得
        List<ApprovalTask> latestTasks = approvalTaskMapper.selectByExample(ex);

        if (!isSameStepFullyApproved(latestTasks, approverTask)) {
            logger.info("PR承認処理完了");
            return;
        }
        // 現在のステップ更新
        if (nextApprovalStepExists(latestTasks, approverTask)) {
            PurchaseRequest step = new PurchaseRequest();
            step.setPrId(prId);
            step.setCurrentStepOrder(currentStep + 1);
            purchaseRequestMapper.updateByPrimaryKeySelective(step);
            logger.info("PR承認ステップ更新");
        }
        // ドキュメントステータス更新
        else {
            PurchaseRequest status = new PurchaseRequest();
            status.setPrId(prId);
            status.setStatus(PurchaseRequestStatus.APPROVED);
            purchaseRequestMapper.updateByPrimaryKeySelective(status);
            logger.info("PR承認ステータス更新");
        }
        logger.info("PR承認処理完了");
    }

    private boolean isSameStepFullyApproved(List<ApprovalTask> tasks, ApprovalTask approverTask) {
        return tasks.stream()
            .filter(t -> t.getDocumentId().equals(approverTask.getDocumentId())
                    && t.getStepOrder().equals(approverTask.getStepOrder()))
            .allMatch(t -> t.getStatus() == ApprovalStatus.APPROVED);
    }

    private boolean nextApprovalStepExists(List<ApprovalTask> tasks, ApprovalTask approverTask) {
        return tasks.stream()
            .anyMatch(t -> t.getDocumentId().equals(approverTask.getDocumentId())
                    && t.getStepOrder().equals(approverTask.getStepOrder() + 1));
    }

}
