package com.example.p2p.dto;

import com.example.p2p.enums.RoleName;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleListItemDto {

    private String roleId;

    private RoleName name;

}
