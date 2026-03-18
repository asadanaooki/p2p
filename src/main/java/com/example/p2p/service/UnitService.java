package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.UnitDetailDto;
import com.example.p2p.dto.UnitListItemDto;
import com.example.p2p.entity.Unit;
import com.example.p2p.entity.UnitExample;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UnitEditForm;
import com.example.p2p.mapper.UnitMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UnitService {

    private UnitMapper unitMapper;

    public List<UnitListItemDto> getUnitList(Boolean status) {
        UnitExample ex = new UnitExample();
        if (status != null) {
            ex.createCriteria().andIsActiveEqualTo(status);
        }
        List<Unit> list = unitMapper.selectByExample(ex);
        return list.stream().map(u -> new UnitListItemDto(
                u.getUnitId(),
                u.getName(),
                u.getIsActive())).toList();
    }

    public void create(String name) {
        UnitExample ex = new UnitExample();
        ex.createCriteria().andNameEqualTo(name);
        // 重複チェック
        if (unitMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        Unit u = new Unit();
        u.setName(name);
        unitMapper.insertSelective(u);
    }

    public UnitDetailDto getUnitDetail(String unitId) {
        Unit unit = unitMapper.selectByPrimaryKey(unitId);
        return new UnitDetailDto(unit.getUnitId(), unit.getName(), unit.getIsActive());
    }

    public void update(String unitId, UnitEditForm form) {
        UnitExample ex = new UnitExample();
        ex.createCriteria().andNameEqualTo(form.getName()).andUnitIdNotEqualTo(unitId);

        // 重複チェック
        if (unitMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        Unit target = new Unit();
        target.setUnitId(unitId);
        target.setName(form.getName());
        target.setIsActive(form.isStatus());

        unitMapper.updateByPrimaryKeySelective(target);
    }

}
