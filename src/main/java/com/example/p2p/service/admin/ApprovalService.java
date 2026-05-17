package com.example.p2p.service.admin;

import java.io.ObjectInputStream.GetField;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.admin.ApprovalWorkflowDto;
import com.example.p2p.enums.DocumentType;
import com.example.p2p.mapper.ApprovalWorkflowMapperCustom;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ApprovalService {

    private ApprovalWorkflowMapperCustom approvalWorkflowMapperCustom;
    
    public ApprovalWorkflowDto getApprovalWorkflowView(DocumentType documentType) {
        return approvalWorkflowMapperCustom.selectApprovalWorkflow(documentType);
    }
}
