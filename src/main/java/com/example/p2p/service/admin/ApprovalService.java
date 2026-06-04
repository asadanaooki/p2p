package com.example.p2p.service.admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private ApprovalWorkflowMapper approvalWorkflowMapper;

    private ApprovalWorkflowMapperCustom approvalWorkflowMapperCustom;

    private ApprovalStepMapper approvalStepMapper;

    private ApprovalStepApproverMapper approvalStepApproverMapper;
    
    private UsersMapperCustom userMapperCustom;

    public ApprovalWorkflowDto getApprovalWorkflowView(DocumentType documentType) {
        ApprovalWorkflowDto dto = Optional.ofNullable(approvalWorkflowMapperCustom.selectApprovalWorkflow(documentType))
            .orElse(new ApprovalWorkflowDto());
        dto.setUserOptions(getApprovalUserOptions(documentType));
        
        return dto;
    }
    
    public List<UserOptionDto> getApprovalUserOptions(DocumentType documentType) {
        return userMapperCustom.selectApprovalUserOptions(documentType);
    }

    @Transactional
    public void create(DocumentType documentType, ApprovalWorkflowCreateForm form) {
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
    }

}
