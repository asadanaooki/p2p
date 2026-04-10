package com.example.p2p.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserListViewDto {

    private List<UserListRowDto> users;
    
    private List<RoleOptionDto> roleOptions;

    private List<Integer> pageNumberList;
    
    private int currentPage;

}
