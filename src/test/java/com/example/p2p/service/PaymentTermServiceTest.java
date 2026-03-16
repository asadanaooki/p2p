package com.example.p2p.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.PaymentTerm;
import com.example.p2p.enums.DueDateType;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.PaymentTermEditForm;
import com.example.p2p.mapper.PaymentTermMapper;

@SpringBootTest
@Transactional
class PaymentTermServiceTest {
    
    @Autowired
    PaymentTermService paymentTermService;

    @Autowired
    PaymentTermMapper paymentTermMapper;

    @Nested
    class Update{
        String paymentTermId = "52434b8c-b0e1-4940-a2d4-c16086414842";
        
        @Test
        void update_duplicate() {
            String duplicateName = "翌月末払い";
            PaymentTermEditForm form = new PaymentTermEditForm(
                    duplicateName,
                    30,
                    DueDateType.NEXT_MONTH_DAY,
                    true
                    );

            assertThatThrownBy(() -> paymentTermService.update(paymentTermId, form))
                .isInstanceOf(BusinessException.class);
        }
        
        @Test
        void update_name_success() {
            String updatedName = "test";
            PaymentTermEditForm form = new PaymentTermEditForm(
                    updatedName,
                    31,
                    DueDateType.NEXT_MONTH_DAY,
                    true
                    );
            
            paymentTermService.update(paymentTermId, form);
            
           PaymentTerm actual = paymentTermMapper.selectByPrimaryKey(paymentTermId);
           assertThat(actual.getName()).isEqualTo("test");
           assertThat(actual.getDays()).isEqualTo(31);
           assertThat(actual.getDueDateType()).isEqualTo(DueDateType.NEXT_MONTH_DAY.toString());
           assertThat(actual.getIsActive()).isEqualTo(true);
        }
        
        @Test
        void update_others_success() {
            String updatedName = "今月末払い";
            PaymentTermEditForm form = new PaymentTermEditForm(
                    updatedName,
                    30,
                    DueDateType.THIS_MONTH_DAY,
                    false
                    );
            
            paymentTermService.update(paymentTermId, form);
            
           PaymentTerm actual = paymentTermMapper.selectByPrimaryKey(paymentTermId);
           assertThat(actual.getName()).isEqualTo(updatedName);
           assertThat(actual.getDays()).isEqualTo(30);
           assertThat(actual.getDueDateType()).isEqualTo(DueDateType.THIS_MONTH_DAY.toString());
           assertThat(actual.getIsActive()).isEqualTo(false);
        }
    }
    


}
