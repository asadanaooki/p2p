package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.UnitListItemDto;
import com.example.p2p.entity.Unit;
import com.example.p2p.entity.UnitExample;
import com.example.p2p.exception.BusinessException;
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
        return list.stream().map(u -> new UnitListItemDto(u.getName(), u.getIsActive())).toList();
    }

    public void create(String name) {
        // 重複チェック
        UnitExample ex = new UnitExample();
        String trimmed = name.strip();
        ex.createCriteria().andNameEqualTo(trimmed);
        if (unitMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        Unit u = new Unit();
        u.setName(trimmed);
        unitMapper.insertSelective(u);
    }

}
