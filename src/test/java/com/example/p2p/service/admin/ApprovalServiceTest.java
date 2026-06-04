package com.example.p2p.service.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.admin.ApprovalStepApproverDto;
import com.example.p2p.dto.admin.ApprovalStepDto;
import com.example.p2p.dto.admin.ApprovalWorkflowDto;
import com.example.p2p.enums.DocumentType;
import com.example.p2p.form.admin.ApprovalStepApproverCreateForm;
import com.example.p2p.form.admin.ApprovalStepCreateForm;
import com.example.p2p.form.admin.ApprovalWorkflowCreateForm;
import com.example.p2p.mapper.ApprovalWorkflowMapperCustom;

@SpringBootTest
@Transactional
class ApprovalServiceTest {

    @Autowired
    ApprovalService approvalService;

    @Autowired
    ApprovalWorkflowMapperCustom approvalWorkflowMapperCustom;

    @Test
    void create() {
        ApprovalWorkflowCreateForm form = new ApprovalWorkflowCreateForm();
        ApprovalStepCreateForm firstStepForm = new ApprovalStepCreateForm();
        ApprovalStepCreateForm secondStepForm = new ApprovalStepCreateForm();
        ApprovalStepApproverCreateForm firstStepApproverForm = new ApprovalStepApproverCreateForm();
        firstStepApproverForm.setUserId("36a1d5d9-15b8-45d5-8ae7-607244bbe36e");
        firstStepApproverForm.setAmountMin(300);
        firstStepApproverForm.setAmountMax(50000);
        ApprovalStepApproverCreateForm secondStepFirstApproverForm = new ApprovalStepApproverCreateForm();
        secondStepFirstApproverForm.setUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
        secondStepFirstApproverForm.setAmountMin(500);
        secondStepFirstApproverForm.setAmountMax(30000);
        ApprovalStepApproverCreateForm secondStepSecondApproverForm = new ApprovalStepApproverCreateForm();
        secondStepSecondApproverForm.setUserId("36a1d5d9-15b8-45d5-8ae7-607244bbe36e");
        secondStepSecondApproverForm.setAmountMin(30000);

        firstStepForm.setName("承認１");
        firstStepForm.setApprovalStepApprovers(List.of(firstStepApproverForm));
        secondStepForm.setName("承認2");
        secondStepForm.setApprovalStepApprovers(List.of(secondStepFirstApproverForm, secondStepSecondApproverForm));

        form.setApprovalSteps(List.of(firstStepForm, secondStepForm));

        approvalService.create(DocumentType.INVOICE, form);

        ApprovalWorkflowDto actual = approvalWorkflowMapperCustom.selectApprovalWorkflow(DocumentType.INVOICE);
        assertThat(actual.getApprovalWorkflowId()).isNotBlank();

        List<ApprovalStepDto> steps = actual.getApprovalSteps();
        assertThat(steps).hasSize(2);

        ApprovalStepDto first = steps.get(0);
        assertThat(first.getApprovalStepId()).isNotBlank();
        assertThat(first.getName()).isEqualTo("承認１");
        assertThat(first.getStepOrder()).isOne();
        assertThat(first.getApprovalStepApprovers()).singleElement().satisfies(s -> {
            assertThat(s.getApprovalStepApproverId()).isNotBlank();
            assertThat(s.getUserId()).isEqualTo("36a1d5d9-15b8-45d5-8ae7-607244bbe36e");
            assertThat(s.getUserName()).isEqualTo("山田 太郎");
            assertThat(s.getEmail()).isEqualTo("siotan0926@gmail.com");
            assertThat(s.getAmountMin()).isEqualTo(300);
            assertThat(s.getAmountMax()).isEqualTo(50000);
        });

        ApprovalStepDto second = steps.get(1);
        assertThat(second.getApprovalStepApprovers()).hasSize(2)
            .extracting(ApprovalStepApproverDto::getUserId)
            .containsExactly("169f1e17-619f-45bf-b6dc-8faed08c404c", "36a1d5d9-15b8-45d5-8ae7-607244bbe36e");
    }

}
