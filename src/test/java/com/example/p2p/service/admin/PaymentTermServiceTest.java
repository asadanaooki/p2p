package com.example.p2p.service.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.PaymentTerm;
import com.example.p2p.entity.PaymentTermExample;
import com.example.p2p.enums.DueDateType;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.PaymentTermCreateForm;
import com.example.p2p.form.PaymentTermEditForm;
import com.example.p2p.mapper.PaymentTermMapper;
import com.example.p2p.service.admin.PaymentTermService;

@SpringBootTest
@Transactional
class PaymentTermServiceTest {

    @Autowired
    PaymentTermService paymentTermService;

    @Autowired
    PaymentTermMapper paymentTermMapper;

    @Nested
    class Create {

        @Test
        void create_duplicate() {
            PaymentTermCreateForm form = new PaymentTermCreateForm();
            form.setName("翌月末払い");
            form.setDays(20);
            form.setDueDateType(DueDateType.NEXT_MONTH_DAY);

            assertThatThrownBy(() -> paymentTermService.create(form))
            .isInstanceOf(BusinessException.class);

        }

        @Test
        void create_success() {
            PaymentTermCreateForm form = new PaymentTermCreateForm();
            form.setName("test");
            form.setDays(10);
            form.setDueDateType(DueDateType.NET_DAYS);
            
            paymentTermService.create(form);
            
            PaymentTermExample ex = new PaymentTermExample();
            ex.createCriteria().andNameEqualTo("test");
           PaymentTerm created = paymentTermMapper.selectByExample(ex).get(0);
           assertThat(created.getPaymentTermId()).isNotBlank();
           assertThat(created.getName()).isEqualTo("test");
           assertThat(created.getDays()).isEqualTo(10);
           assertThat(created.getDueDateType()).isEqualTo(DueDateType.NET_DAYS.toString());
           assertThat(created.getIsActive()).isTrue();
           assertThat(created.getCreatedAt()).isNotNull();
           assertThat(created.getUpdatedAt()).isNotNull();
           
        }

    }

    @Nested
    class Update {

        String paymentTermId = "33333333-3333-3333-3333-333333333333";

        @Test
        void update_duplicate() {
            String duplicateName = "翌月末払い";
            PaymentTermEditForm form = new PaymentTermEditForm(duplicateName, 30, DueDateType.NEXT_MONTH_DAY, true);

            assertThatThrownBy(() -> paymentTermService.update(paymentTermId, form))
                .isInstanceOf(BusinessException.class);
        }

        @Test
        void update_name_success() {
            String updatedName = "test";
            PaymentTermEditForm form = new PaymentTermEditForm(updatedName, 31, DueDateType.NEXT_MONTH_DAY, true);

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
            PaymentTermEditForm form = new PaymentTermEditForm(updatedName, 30, DueDateType.THIS_MONTH_DAY, false);

            paymentTermService.update(paymentTermId, form);

            PaymentTerm actual = paymentTermMapper.selectByPrimaryKey(paymentTermId);
            assertThat(actual.getName()).isEqualTo(updatedName);
            assertThat(actual.getDays()).isEqualTo(30);
            assertThat(actual.getDueDateType()).isEqualTo(DueDateType.THIS_MONTH_DAY.toString());
            assertThat(actual.getIsActive()).isEqualTo(false);
        }

    }

}
