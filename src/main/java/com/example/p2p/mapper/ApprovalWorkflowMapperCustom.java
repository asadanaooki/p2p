package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.admin.ApprovalTaskCandidateDto;
import com.example.p2p.dto.admin.ApprovalWorkflowDto;
import com.example.p2p.enums.DocumentType;

@Mapper
public interface ApprovalWorkflowMapperCustom {

    ApprovalWorkflowDto selectApprovalWorkflow(DocumentType documentType);

    List<ApprovalTaskCandidateDto> selectApprovalTaskCandidates(DocumentType documentType, int totalAmountExcludingTax);

}
