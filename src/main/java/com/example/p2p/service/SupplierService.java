package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.p2p.dto.PaymentTermOptionDto;
import com.example.p2p.dto.SupplierDetailDto;
import com.example.p2p.dto.SupplierListItemDto;
import com.example.p2p.entity.Supplier;
import com.example.p2p.entity.SupplierExample;
import com.example.p2p.entity.SupplierExample.Criteria;
import com.example.p2p.form.SupplierEditForm;
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
        return list.stream().map(s -> new SupplierListItemDto(
                s.getSupplierId(),
                s.getName(),
                s.getEmail(),
                s.getPhoneNumber(),
                s.getIsActive())).toList();
    }
    
    public SupplierDetailDto getSupplierDetail(String supplierId) {
        return supplierMapperCustom.selectSupplierDetail(supplierId);
    }
    
    public List<PaymentTermOptionDto> getPaymentTermOptions(){
        return paymentTermMapperCustom.selectPaymentTermOptions();
    }

    // public void create(String name) {
    // UnitExample ex = new UnitExample();
    // ex.createCriteria().andNameEqualTo(name);
    // // 重複チェック
    // if (unitMapper.countByExample(ex) > 0) {
    // throw new BusinessException();
    // }
    // Unit u = new Unit();
    // u.setName(name);
    // unitMapper.insertSelective(u);
    // }
    //
    // public UnitDetailDto getUnitDetail(String unitId) {
    // Unit unit = unitMapper.selectByPrimaryKey(unitId);
    // return new UnitDetailDto(unit.getUnitId(), unit.getName(), unit.getIsActive());
    // }
    //
    public void update(String supplierId, SupplierEditForm form) {
        Supplier update = new Supplier();
        update.setSupplierId(supplierId);
        update.setName(form.getName());
        update.setEmail(form.getEmail());
        update.setPhoneNumber(form.getPhoneNumber());
        update.setPostalCode(form.getPostalCode());
        update.setPrefecture(form.getPrefecture());
        update.setCity(form.getCity());
        update.setStreetAddress(form.getStreetAddress());
        update.setBuildingName(form.getBuildingName());
        update.setPaymentTermId(form.getPaymentTermId());
        update.setIsActive(form.isStatus());

        supplierMapperCustom.update(update);
     }

}
