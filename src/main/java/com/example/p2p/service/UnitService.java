package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.UnitListItemDto;
import com.example.p2p.entity.Unit;
import com.example.p2p.entity.UnitExample;
import com.example.p2p.mapper.UnitMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UnitService {

    private UnitMapper unitMapper;

    public List<UnitListItemDto> getUnitList(Boolean status) {
        UnitExample ex =new UnitExample();
        if (status != null) {
            ex.createCriteria().andIsActiveEqualTo(status);
        }
        List<Unit> list = unitMapper.selectByExample(ex);
        return list.stream().map(u -> 
            new UnitListItemDto(u.getName(), u.getIsActive()))
                .toList();
    }

}
