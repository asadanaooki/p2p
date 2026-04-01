package com.example.p2p.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.RoleDetailDto;
import com.example.p2p.dto.RoleListItemDto;
import com.example.p2p.entity.Role;
import com.example.p2p.entity.RoleExample;
import com.example.p2p.form.RoleEditForm;
import com.example.p2p.mapper.RoleMapper;
import com.example.p2p.mapper.RoleMapperCustom;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleService {

    private RoleMapper roleMapper;
    
    private RoleMapperCustom roleMapperCustom;
    
    private ModelMapper modelMapper;

    public List<RoleListItemDto> getRoleList() {
        RoleExample ex = new RoleExample();
        ex.setOrderByClause("name asc");
        return roleMapper.selectByExample(ex)
            .stream()
            .map(r -> new RoleListItemDto(r.getRoleId(), r.getName()))
            .toList();
    }

//    public void create(String name) {
//        UnitExample ex = new UnitExample();
//        ex.createCriteria().andNameEqualTo(name);
//        // 重複チェック
//        if (unitMapper.countByExample(ex) > 0) {
//            throw new BusinessException();
//        }
//        Unit u = new Unit();
//        u.setName(name);
//        unitMapper.insertSelective(u);
//    }
//
    public RoleDetailDto getRoleDetail(String unitId) {
        return roleMapperCustom.selectRoleDetail(unitId);
    }
    
    public void update(String roleId, RoleEditForm form) {
       Role role = modelMapper.map(form, Role.class);
       role.setRoleId(roleId);
       roleMapper.updateByPrimaryKeySelective(role);
    }
    
//
//    public void update(String unitId, UnitEditForm form) {
//        UnitExample ex = new UnitExample();
//        ex.createCriteria().andNameEqualTo(form.getName()).andUnitIdNotEqualTo(unitId);
//
//        // 重複チェック
//        if (unitMapper.countByExample(ex) > 0) {
//            throw new BusinessException();
//        }
//        Unit target = new Unit();
//        target.setUnitId(unitId);
//        target.setName(form.getName());
//        target.setIsActive(form.isStatus());
//
//        unitMapper.updateByPrimaryKeySelective(target);
//    }

}
