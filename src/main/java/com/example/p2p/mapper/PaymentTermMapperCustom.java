package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.PaymentTermOptionDto;

@Mapper
public interface PaymentTermMapperCustom {

    List<PaymentTermOptionDto> selectPaymentTermOptions();
}
