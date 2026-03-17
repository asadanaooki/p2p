package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.PaymentTermDetailDto;
import com.example.p2p.dto.PaymentTermListItemDto;
import com.example.p2p.dto.UnitDetailDto;
import com.example.p2p.entity.PaymentTerm;
import com.example.p2p.entity.PaymentTermExample;
import com.example.p2p.entity.Unit;
import com.example.p2p.enums.DueDateType;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.PaymentTermEditForm;
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
                    DueDateType.valueOf(pt.getDueDateType()).getLabel(),
                    pt.getIsActive()))
            .toList();
    }
    
    public PaymentTermDetailDto getPaymentTermDetail(String paymentTermId) {
        PaymentTerm pt = paymentTermMapper.selectByPrimaryKey(paymentTermId);
        return new PaymentTermDetailDto(
                pt.getPaymentTermId(),
                pt.getName(),
                pt.getDays(),
                DueDateType.valueOf(pt.getDueDateType()),
                pt.getIsActive());
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
        pt.setDueDateType(form.getDueDateType().toString());
        pt.setIsActive(form.isActive());
        paymentTermMapper.updateByPrimaryKeySelective(pt);
    }

    
}
