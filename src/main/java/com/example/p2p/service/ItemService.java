package com.example.p2p.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.ItemDetailDto;
import com.example.p2p.dto.ItemEditViewDto;
import com.example.p2p.dto.ItemListItemDto;
import com.example.p2p.dto.ItemListViewDto;
import com.example.p2p.entity.Item;
import com.example.p2p.entity.ItemExample;
import com.example.p2p.form.ItemEditForm;
import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.mapper.ItemMapper;
import com.example.p2p.mapper.ItemMapperCustom;
import com.example.p2p.mapper.SupplierMapperCustom;
import com.example.p2p.mapper.UnitMapperCustom;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemService {

    private ItemMapperCustom itemMapperCustom;

    private ItemMapper itemMapper;

    private SupplierMapperCustom supplierMapperCustom;
    
    private UnitMapperCustom unitMapperCustom;
    
    private ModelMapper modelMapper;


    public ItemListViewDto searchItems(ItemSearchForm form) {
        ItemListViewDto dto = new ItemListViewDto();
        int page = form.getPage();
        List<ItemListItemDto> items = itemMapperCustom.selectItems(form);
        dto.setItems(items);
        dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());
        dto.setCurrentPage(page);
        dto.setPageNumberList(
                CommonUtil.createPageNumbers((int) itemMapper.countByExample(new ItemExample()),
                form.getSize(), page, 2));
        return dto;
    }
    
    public ItemEditViewDto prepareItemEditView(String itemId) {
       Item item = itemMapper.selectByPrimaryKey(itemId);
       ItemEditViewDto dto = modelMapper.map(item, ItemEditViewDto.class);
       dto.setUnitOptions(unitMapperCustom.selectUnitOptions());
       dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());
       
       return dto;
    }
    
    public Map<String, List> getOptions(){
        Map<String, List> map = new LinkedHashMap<String, List>();
        map.put("unitOptions", unitMapperCustom.selectUnitOptions());
        map.put("supplierOptions", supplierMapperCustom.selectSupplierOptions());
        return map;
    }

    public ItemDetailDto getItemDetail(String itemId) {
        return itemMapperCustom.selectItemDetail(itemId);
    }

    public void update(String itemId, ItemEditForm form) {
        Item item = new Item();
        item.setItemId(itemId);
        item.setName(form.getName());
        item.setKind(form.getKind().name());
        item.setUnitId(form.getUnitId());
        item.setPrice(form.getPrice());
        item.setSupplierId(form.getSupplierId());
        item.setDescription(form.getDescription());
        item.setIsActive(form.isActive());

        itemMapper.updateByPrimaryKeySelective(item);
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
