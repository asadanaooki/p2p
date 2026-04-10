package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.RoleDetailDto;
import com.example.p2p.dto.RoleOptionDto;

@Mapper
public interface RoleMapperCustom {

    RoleDetailDto selectRoleDetail(String roleId);
    
   List<RoleOptionDto> selectRoleOptions();
}
