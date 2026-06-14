package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.ApprovalTask;
import com.example.p2p.entity.PurchaseRequest;
import com.example.p2p.entity.Users;
import com.example.p2p.enums.ApprovalStatus;
import com.example.p2p.enums.BusinessErrorCode;
import com.example.p2p.enums.DocumentType;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.mapper.ApprovalTaskMapper;
import com.example.p2p.mapper.PurchaseRequestMapper;
import com.example.p2p.mapper.UsersMapper;

@SpringBootTest
@Transactional
class ApprovalActionServiceTest {

    @Autowired
    ApprovalActionService approvalActionService;

    @Autowired
    ApprovalTaskMapper approvalTaskMapper;

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    PurchaseRequestMapper purchaseRequestMapper;

    @BeforeEach
    void setup() {
        String prId = "88bfbcf6-2be6-4d31-8a46-155a7b58ab93";
        Users user = new Users();
        user.setUserId("6fe99043-cbd1-49c0-96d4-c156c58a8e60");
        user.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");
        usersMapper.updateByPrimaryKeySelective(user);

        ApprovalTask firstStep1 = new ApprovalTask();
        firstStep1.setDocumentId(prId);
        firstStep1.setStepOrder(1);
        firstStep1.setUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
        firstStep1.setDocumentType(DocumentType.PR);
        firstStep1.setStatus(ApprovalStatus.PENDING);
        firstStep1.setStepName("test1段目承認");
        approvalTaskMapper.insertSelective(firstStep1);

        ApprovalTask firstStep2 = new ApprovalTask();
        firstStep2.setDocumentId(prId);
        firstStep2.setStepOrder(1);
        firstStep2.setUserId("36a1d5d9-15b8-45d5-8ae7-607244bbe36e");
        firstStep2.setDocumentType(DocumentType.PR);
        firstStep2.setStatus(ApprovalStatus.PENDING);
        firstStep2.setStepName("test1段目承認");
        approvalTaskMapper.insertSelective(firstStep2);

        ApprovalTask secondStep1 = new ApprovalTask();
        secondStep1.setDocumentId(prId);
        secondStep1.setStepOrder(2);
        secondStep1.setUserId("6fe99043-cbd1-49c0-96d4-c156c58a8e60");
        secondStep1.setDocumentType(DocumentType.PR);
        secondStep1.setStatus(ApprovalStatus.PENDING);
        secondStep1.setStepName("test2段目承認");
        approvalTaskMapper.insertSelective(secondStep1);
    }

    @Nested
    class Approve {

        String prId = "88bfbcf6-2be6-4d31-8a46-155a7b58ab93";

        String userId = "169f1e17-619f-45bf-b6dc-8faed08c404c";
        
        

        @Test
        void approve_documentStatusInvalid() {
            PurchaseRequest pr = new PurchaseRequest();
            pr.setPrId(prId);
            pr.setStatus(PurchaseRequestStatus.APPROVED);
            purchaseRequestMapper.updateByPrimaryKeySelective(pr);

            assertThatThrownBy(() -> approvalActionService.approvePR(prId, userId))
                .isInstanceOfSatisfying(BusinessException.class, e -> {
                    assertThat(e.getErrorCode()).isEqualTo(BusinessErrorCode.PURCHASE_REQUEST_NOT_PENDING);
                });
        }

        @Test
        void approve_approvalTaskStatusInvalid() {
            ApprovalTask task = new ApprovalTask();
            task.setDocumentId(prId);
            task.setStepOrder(1);
            task.setUserId(userId);
            task.setStatus(ApprovalStatus.APPROVED);
            approvalTaskMapper.updateByPrimaryKeySelective(task);

            assertThatThrownBy(() -> approvalActionService.approvePR(prId, userId))
                .isInstanceOfSatisfying(BusinessException.class, e -> {
                    assertThat(e.getErrorCode()).isEqualTo(BusinessErrorCode.APPROVAL_ALREADY_PROCESSED);
                });
        }

        @Test
        void approve_notCurrentStep() {
            PurchaseRequest pr = new PurchaseRequest();
            pr.setPrId(prId);
            pr.setCurrentStepOrder(2);
            purchaseRequestMapper.updateByPrimaryKeySelective(pr);

            assertThatThrownBy(() -> approvalActionService.approvePR(prId, userId))
                .isInstanceOfSatisfying(BusinessException.class, e -> {
                    assertThat(e.getErrorCode()).isEqualTo(BusinessErrorCode.APPROVAL_NOT_CURRENT_STEP);
                });
        }

    }

}
