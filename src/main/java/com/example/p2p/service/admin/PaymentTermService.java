package com.example.p2p.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.admin.PaymentTermDetailDto;
import com.example.p2p.dto.admin.PaymentTermListItemDto;
import com.example.p2p.entity.PaymentTerm;
import com.example.p2p.entity.PaymentTermExample;
import com.example.p2p.enums.DueDateType;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.admin.PaymentTermCreateForm;
import com.example.p2p.form.admin.PaymentTermEditForm;
import com.example.p2p.mapper.PaymentTermMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PaymentTermService {

    private PaymentTermMapper paymentTermMapper;

    public List<PaymentTermListItemDto> getPaymentTerms() {
        PaymentTermExample ex = new PaymentTermExample();
        ex.setOrderByClause("name asc");
       return paymentTermMapper.selectByExample(ex)
            .stream()
            .map(pt -> new PaymentTermListItemDto(
                    pt.getPaymentTermId(),
                    pt.getName(),
                    pt.getDays(),
                    pt.getDueDateType().getLabel(),
                    pt.getIsActive()))
            .toList();
    }
    
    public PaymentTermDetailDto getPaymentTermDetail(String paymentTermId) {
        PaymentTerm pt = paymentTermMapper.selectByPrimaryKey(paymentTermId);
        return new PaymentTermDetailDto(
                pt.getPaymentTermId(),
                pt.getName(),
                pt.getDays(),
                pt.getDueDateType(),
                pt.getIsActive());
    }
    
    public void create(PaymentTermCreateForm form) {
        PaymentTermExample ex = new PaymentTermExample();
        ex.createCriteria().andNameEqualTo(form.getName());
        // 重複チェック
        if (paymentTermMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        PaymentTerm newPaymentTerm = new PaymentTerm();
        newPaymentTerm.setName(form.getName());
        newPaymentTerm.setDays(form.getDays());
        newPaymentTerm.setDueDateType(form.getDueDateType());
        
        paymentTermMapper.insertSelective(newPaymentTerm);
    }
    
    public void update(String paymentTermId, PaymentTermEditForm form) {
        PaymentTermExample ex = new PaymentTermExample();
        ex.createCriteria().andNameEqualTo(form.getName()).andPaymentTermIdNotEqualTo(paymentTermId);
        // 重複チェック
        if (paymentTermMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        
        PaymentTerm pt = new PaymentTerm();
        pt.setPaymentTermId(paymentTermId);
        pt.setName(form.getName());
        pt.setDays(form.getDays());
        pt.setDueDateType(form.getDueDateType());
        pt.setIsActive(form.isActive());
        paymentTermMapper.updateByPrimaryKeySelective(pt);
    }

    
}
