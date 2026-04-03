package com.example.p2p.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.RoleDetailDto;

@Mapper
public interface RoleMapperCustom {

    RoleDetailDto selectRoleDetail(String roleId);
}
