package com.example.p2p.service.admin;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.admin.RoleDetailDto;
import com.example.p2p.dto.admin.RoleListItemDto;
import com.example.p2p.entity.Role;
import com.example.p2p.entity.RoleExample;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.admin.RoleUpsertForm;
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

    public void create(RoleUpsertForm form) {
        RoleExample ex = new RoleExample();
        ex.createCriteria().andNameEqualTo(form.getName());
        // 重複チェック
        if (roleMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        Role role = modelMapper.map(form, Role.class);
        roleMapper.insertSelective(role);
    }

    public RoleDetailDto getRoleDetail(String unitId) {
        return roleMapperCustom.selectRoleDetail(unitId);
    }

    public void update(String roleId, RoleUpsertForm form) {
        RoleExample ex = new RoleExample();
        ex.createCriteria().andNameEqualTo(form.getName()).andRoleIdNotEqualTo(roleId);
        // 重複チェック
        if (roleMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        Role role = modelMapper.map(form, Role.class);
        role.setRoleId(roleId);
        roleMapper.updateByPrimaryKeySelective(role);
    }

}
