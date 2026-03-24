package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.ItemListItemDto;
import com.example.p2p.dto.ItemListViewDto;
import com.example.p2p.entity.ItemExample;
import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.mapper.ItemMapper;
import com.example.p2p.mapper.ItemMapperCustom;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemService {
    
    private ItemMapperCustom itemMapperCustom;
    
    private ItemMapper itemMapper;

    public ItemListViewDto searchItems(ItemSearchForm form) {
        ItemListViewDto dto = new ItemListViewDto();
        int page = form.getPage();
        List<ItemListItemDto> items = itemMapperCustom.selectItems(form);
        dto.setItems(items);
        dto.setCurrentPage(page);
        dto.setPageNumberList(CommonUtil.createPageNumbers(
                (int) itemMapper.countByExample(new ItemExample()),
                form.getSize(),
                page,
                2));
        return dto;
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
