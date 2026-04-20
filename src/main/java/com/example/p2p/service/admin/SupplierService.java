package com.example.p2p.service.admin;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.p2p.dto.admin.PaymentTermOptionDto;
import com.example.p2p.dto.admin.SupplierDetailDto;
import com.example.p2p.dto.admin.SupplierListItemDto;
import com.example.p2p.entity.Supplier;
import com.example.p2p.entity.SupplierExample;
import com.example.p2p.entity.SupplierExample.Criteria;
import com.example.p2p.form.admin.SupplierCreateForm;
import com.example.p2p.form.admin.SupplierEditForm;
import com.example.p2p.mapper.PaymentTermMapperCustom;
import com.example.p2p.mapper.SupplierMapper;
import com.example.p2p.mapper.SupplierMapperCustom;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SupplierService {

    private SupplierMapper supplierMapper;

    private SupplierMapperCustom supplierMapperCustom;

    private PaymentTermMapperCustom paymentTermMapperCustom;

    private ModelMapper modelMapper;

    public List<SupplierListItemDto> getSuppliers(Boolean status, String keyword) {
        SupplierExample ex = new SupplierExample();
        Criteria criteria = ex.createCriteria();
        if (status != null) {
            criteria.andIsActiveEqualTo(status);
        }
        if (StringUtils.hasText(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        ex.setOrderByClause("name asc");
        List<Supplier> list = supplierMapper.selectByExample(ex);
        return list.stream()
            .map(s -> new SupplierListItemDto(
                    s.getSupplierId(),
                    s.getName(),
                    s.getEmail(),
                    s.getPhoneNumber(),
                    s.getIsActive()))
            .toList();
    }

    public SupplierDetailDto getSupplierDetail(String supplierId) {
        return supplierMapperCustom.selectSupplierDetail(supplierId);
    }

    public List<PaymentTermOptionDto> getPaymentTermOptions() {
        return paymentTermMapperCustom.selectPaymentTermOptions();
    }

    public void create(SupplierCreateForm form) {
        Supplier s = modelMapper.map(form, Supplier.class);
        supplierMapper.insertSelective(s);
    }

    public void update(String supplierId, SupplierEditForm form) {
        modelMapper.typeMap(SupplierEditForm.class, Supplier.class)
        .addMappings(mapper -> {
            mapper.map(src -> src.isStatus(), Supplier::setIsActive);
        });
        Supplier s = modelMapper.map(form, Supplier.class);
        s.setSupplierId(supplierId);

        supplierMapperCustom.update(s);
    }

}
