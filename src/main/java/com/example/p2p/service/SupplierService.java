package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.SupplierDetailDto;
import com.example.p2p.dto.SupplierListItemDto;
import com.example.p2p.entity.Supplier;
import com.example.p2p.entity.SupplierExample;
import com.example.p2p.entity.SupplierExample.Criteria;
import com.example.p2p.mapper.SupplierMapper;
import com.example.p2p.mapper.SupplierMapperCustom;

import ch.qos.logback.core.util.StringUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SupplierService {

    private SupplierMapper supplierMapper;
    
    private SupplierMapperCustom supplierMapperCustom;

    public List<SupplierListItemDto> getSuppliers(Boolean status, String keyword) {
        SupplierExample ex = new SupplierExample();
        Criteria criteria = ex.createCriteria();
        if (status != null) {
            criteria.andIsActiveEqualTo(status);
        }
        if (StringUtil.notNullNorEmpty(keyword)) {
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
    // public void update(String unitId, UnitEditForm form) {
    // UnitExample ex = new UnitExample();
    // ex.createCriteria().andNameEqualTo(form.getName()).andUnitIdNotEqualTo(unitId);
    //
    // // 重複チェック
    // if (unitMapper.countByExample(ex) > 0) {
    // throw new BusinessException();
    // }
    // Unit target = new Unit();
    // target.setUnitId(unitId);
    // target.setName(form.getName());
    // target.setIsActive(form.isStatus());
    //
    // unitMapper.updateByPrimaryKeySelective(target);
    // }

}
