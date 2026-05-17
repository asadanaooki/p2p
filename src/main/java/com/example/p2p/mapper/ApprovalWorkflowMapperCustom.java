package com.example.p2p.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.admin.ApprovalWorkflowDto;
import com.example.p2p.enums.DocumentType;

@Mapper
public interface ApprovalWorkflowMapperCustom {

    ApprovalWorkflowDto selectApprovalWorkflow(DocumentType documentType);
}
