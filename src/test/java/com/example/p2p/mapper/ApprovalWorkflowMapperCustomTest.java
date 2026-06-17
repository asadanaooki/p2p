package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.admin.ApprovalStepApproverDto;
import com.example.p2p.dto.admin.ApprovalStepDto;
import com.example.p2p.dto.admin.ApprovalTaskCandidateDto;
import com.example.p2p.dto.admin.ApprovalWorkflowDto;
import com.example.p2p.entity.ApprovalStepApprover;
import com.example.p2p.enums.DocumentType;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApprovalWorkflowMapperCustomTest {

    @Autowired
    ApprovalStepApproverMapper approvalStepApproverMapper;

    @Autowired
    ApprovalWorkflowMapperCustom approvalWorkflowMapperCustom;

    @Test
    void selectApprovalWorkflow() {
        ApprovalWorkflowDto actual = approvalWorkflowMapperCustom.selectApprovalWorkflow(DocumentType.PO);
        assertThat(actual.getApprovalWorkflowId()).isEqualTo("22222222-2222-2222-2222-222222222222");

        List<ApprovalStepDto> steps = actual.getApprovalSteps();
        assertThat(steps).hasSize(3);
        ApprovalStepDto firstStep = steps.get(0);
        assertThat(firstStep.getApprovalStepId()).isEqualTo("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1");
        assertThat(firstStep.getName()).isEqualTo("発注内容確認");
        assertThat(firstStep.getStepOrder()).isEqualTo(1);

        assertThat(steps.get(2).getApprovalStepApprovers()).hasSize(2);

        ApprovalStepApproverDto approver = steps.get(2).getApprovalStepApprovers().get(0);
        assertThat(approver.getApprovalStepApproverId()).isEqualTo("ffffffff-ffff-ffff-ffff-ffffffffff04");
        assertThat(approver.getUserId()).isEqualTo("169f1e17-619f-45bf-b6dc-8faed08c404c");
        assertThat(approver.getUserName()).isEqualTo("佐藤 花子");
        assertThat(approver.getEmail()).isEqualTo("sato.hanako@example.com");
        assertThat(approver.getAmountMin()).isEqualTo(300_000);
        assertThat(approver.getAmountMax()).isNull();

    }

    @Nested
    class SelectApprovalTaskCandidates {

        @Test
        void selectApprovalTaskCandidates_withoutMax() {
            List<ApprovalTaskCandidateDto> actual = approvalWorkflowMapperCustom
                .selectApprovalTaskCandidates(DocumentType.INVOICE, 2000);
            assertThat(actual).hasSize(2);
        }

        @Test
        void selectApprovalTaskCandidates_withMax() {
            ApprovalStepApprover entity = new ApprovalStepApprover();
            entity.setApprovalStepApproverId("eeeeeeee-eeee-eeee-eeee-eeeeeeeeee02");
            entity.setAmountMax(10000);
            entity.setAmountMax(50000);
            approvalStepApproverMapper.updateByPrimaryKeySelective(entity);

            List<ApprovalTaskCandidateDto> actual = approvalWorkflowMapperCustom
                .selectApprovalTaskCandidates(DocumentType.INVOICE, 20000);
            assertThat(actual).hasSize(2);
            assertThat(actual.stream()
                .filter(d -> d.getUserId().equals("36a1d5d9-15b8-45d5-8ae7-607244bbe36e") && d.getStepOrder() == 2
                        && d.getStepName().equals("支払承認"))
                .count()).isOne();
        }

    }

}
