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
import com.example.p2p.form.ItemUpsertForm;
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

    public void update(String itemId, ItemUpsertForm form) {
        Item item = toItem(form);
        item.setItemId(itemId);
        itemMapper.updateByPrimaryKeySelective(item);
    }

    public void create(ItemUpsertForm form) {
        itemMapper.insertSelective(toItem(form));
    }
    
    private Item toItem(ItemUpsertForm form) {
        Item item = new Item();
        item.setName(form.getName());
        item.setKind(form.getKind().name());
        item.setUnitId(form.getUnitId());
        item.setPrice(form.getPrice());
        item.setSupplierId(form.getSupplierId());
        item.setDescription(form.getDescription());
        item.setIsActive(form.getActive());
        return item;
    }

}
