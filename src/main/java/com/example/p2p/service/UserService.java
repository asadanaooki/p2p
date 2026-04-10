package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.UserDetailDto;
import com.example.p2p.dto.UserListRowDto;
import com.example.p2p.dto.UserListViewDto;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.mapper.RoleMapperCustom;
import com.example.p2p.mapper.UsersMapper;
import com.example.p2p.mapper.UsersMapperCustom;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private UsersMapper usersMapper;

    private UsersMapperCustom usersMapperCustom;

    private RoleMapperCustom roleMapperCustom;

    public UserListViewDto searchUsers(UserSearchForm form) {
        UserListViewDto dto = new UserListViewDto();
        int page = form.getPage();
        List<UserListRowDto> users = usersMapperCustom.selectUsers(form);
        dto.setUsers(users);
        dto.setRoleOptions(roleMapperCustom.selectRoleOptions());
        dto.setCurrentPage(page);
        dto.setPageNumberList(CommonUtil.createPageNumbers((int) usersMapper.countByExample(new UsersExample()),
                form.getSize(), page, 2));
        return dto;
    }

    public UserDetailDto getUserDetail(String userId) {
        return usersMapperCustom.selectUserDetail(userId);
    }

}
