package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.entity.ApprovalTask;
import com.example.p2p.entity.ApprovalTaskExample;
import com.example.p2p.entity.ApprovalTaskKey;
import com.example.p2p.enums.ApprovalStatus;
import com.example.p2p.enums.DocumentType;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApprovalTaskMapperCustomTest {

    @Autowired
    ApprovalTaskMapperCustom approvalTaskMapperCustom;
    
    @Autowired
    ApprovalTaskMapper approvalTaskMapper;
    
    @Test
    void bulkInsert() {
        ApprovalTask step1Task = new ApprovalTask();
        step1Task.setDocumentId("6b2c5959-233f-4b54-8a9b-98f4a1b13c40");
        step1Task.setStepOrder(1);
        step1Task.setUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
        step1Task.setDocumentType(DocumentType.PR);
        step1Task.setStatus(ApprovalStatus.PENDING);
        step1Task.setComment("testコメント");
        
        ApprovalTask step2Task = new ApprovalTask();
        step2Task.setDocumentId("6b2c5959-233f-4b54-8a9b-98f4a1b13c40");
        step2Task.setStepOrder(2);
        step2Task.setUserId("36a1d5d9-15b8-45d5-8ae7-607244bbe36e");
        step2Task.setDocumentType(DocumentType.PR);
        step2Task.setStatus(ApprovalStatus.PENDING);
        
        approvalTaskMapperCustom.bulkInsert(List.of(step1Task, step2Task));
        
        ApprovalTaskExample ex = new ApprovalTaskExample();
        ex.createCriteria().andDocumentIdEqualTo("6b2c5959-233f-4b54-8a9b-98f4a1b13c40");
        assertThat(approvalTaskMapper.selectByExample(ex)).hasSize(2);
        
        ApprovalTaskKey key = new ApprovalTaskKey();
        key.setDocumentId("6b2c5959-233f-4b54-8a9b-98f4a1b13c40");
        key.setStepOrder(1);
        key.setUserId("169f1e17-619f-45bf-b6dc-8faed08c404c");
        ApprovalTask saveStep1Task = approvalTaskMapper.selectByPrimaryKey(key);
        
        assertThat(saveStep1Task.getDocumentType()).isEqualTo(DocumentType.PR);
        assertThat(saveStep1Task.getStatus()).isEqualTo(ApprovalStatus.PENDING);
        assertThat(saveStep1Task.getComment()).isEqualTo("testコメント");
        assertThat(saveStep1Task.getCreatedAt()).isNotNull();
        assertThat(saveStep1Task.getUpdatedAt()).isNotNull();
        
    }
}
