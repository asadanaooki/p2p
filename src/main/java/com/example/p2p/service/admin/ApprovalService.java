package com.example.p2p.service.admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.admin.ApprovalWorkflowDto;
import com.example.p2p.dto.admin.UserOptionDto;
import com.example.p2p.entity.ApprovalStep;
import com.example.p2p.entity.ApprovalStepApprover;
import com.example.p2p.entity.ApprovalWorkflow;
import com.example.p2p.entity.ApprovalWorkflowExample;
import com.example.p2p.enums.DocumentType;
import com.example.p2p.form.admin.ApprovalStepApproverCreateForm;
import com.example.p2p.form.admin.ApprovalStepCreateForm;
import com.example.p2p.form.admin.ApprovalWorkflowCreateForm;
import com.example.p2p.mapper.ApprovalStepApproverMapper;
import com.example.p2p.mapper.ApprovalStepMapper;
import com.example.p2p.mapper.ApprovalWorkflowMapper;
import com.example.p2p.mapper.ApprovalWorkflowMapperCustom;
import com.example.p2p.mapper.UsersMapperCustom;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class);

    private ApprovalWorkflowMapper approvalWorkflowMapper;

    private ApprovalWorkflowMapperCustom approvalWorkflowMapperCustom;

    private ApprovalStepMapper approvalStepMapper;

    private ApprovalStepApproverMapper approvalStepApproverMapper;
    
    private UsersMapperCustom userMapperCustom;

    public ApprovalWorkflowDto getApprovalWorkflowView(DocumentType documentType) {
        logger.debug("承認ワークフロー表示情報取得開始");

        ApprovalWorkflowDto dto = Optional.ofNullable(approvalWorkflowMapperCustom.selectApprovalWorkflow(documentType))
            .orElse(new ApprovalWorkflowDto());
        dto.setUserOptions(getApprovalUserOptions(documentType));

        logger.debug("承認ワークフロー表示情報取得完了");
        
        return dto;
    }
    
    public List<UserOptionDto> getApprovalUserOptions(DocumentType documentType) {
        logger.debug("承認ユーザー選択肢取得開始");

        List<UserOptionDto> userOptions = userMapperCustom.selectApprovalUserOptions(documentType);

        logger.debug("承認ユーザー選択肢取得完了");
        return userOptions;
    }

    @Transactional
    public void create(DocumentType documentType, ApprovalWorkflowCreateForm form) {
        logger.info("承認ワークフロー登録処理開始");

        // ワークフロー削除
        ApprovalWorkflowExample ex = new ApprovalWorkflowExample();
        ex.createCriteria().andDocumentTypeEqualTo(documentType);
        approvalWorkflowMapper.deleteByExample(ex);
        
        // 作成
        String approvalWorkflowId = UUID.randomUUID().toString();
        ApprovalWorkflow workflow = new ApprovalWorkflow();
        workflow.setApprovalWorkflowId(approvalWorkflowId);
        workflow.setDocumentType(documentType);

        approvalWorkflowMapper.insertSelective(workflow);

        int stepOrder = 1;
        for (ApprovalStepCreateForm stepForm : form.getApprovalSteps()) {
            String approvalStepId = UUID.randomUUID().toString();
            ApprovalStep step = new ApprovalStep();
            step.setApprovalStepId(approvalStepId);
            step.setApprovalWorkflowId(approvalWorkflowId);
            step.setName(stepForm.getName());
            step.setStepOrder(stepOrder++);

            approvalStepMapper.insertSelective(step);

            for (ApprovalStepApproverCreateForm approverForm : stepForm.getApprovalStepApprovers()) {
                String approvalStepApproverId = UUID.randomUUID().toString();
                ApprovalStepApprover approver = new ApprovalStepApprover();
                approver.setApprovalStepApproverId(approvalStepApproverId);
                approver.setApprovalStepId(approvalStepId);
                approver.setUserId(approverForm.getUserId());
                approver.setAmountMin(approverForm.getAmountMin());
                approver.setAmountMax(approverForm.getAmountMax());

                approvalStepApproverMapper.insertSelective(approver);
            }
        }

        logger.info("承認ワークフロー登録処理完了");
    }

}
