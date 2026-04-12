package com.example.p2p.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.RoleOptionDto;
import com.example.p2p.dto.UserDetailDto;
import com.example.p2p.dto.UserListRowDto;
import com.example.p2p.dto.UserListViewDto;
import com.example.p2p.entity.Users;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.form.UserUpsertForm;
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

    private PasswordEncoder passwordEncoder;

    private ModelMapper modelMapper;

    public UserListViewDto searchUsers(UserSearchForm form) {
        UserListViewDto dto = new UserListViewDto();
        int page = form.getPage();
        List<UserListRowDto> users = usersMapperCustom.selectUsers(form);
        dto.setUsers(users);
        dto.setRoleOptions(roleMapperCustom.selectRoleOptions());
        dto.setCurrentPage(page);
        dto.setPageNumberList(
                CommonUtil.createPageNumbers(usersMapperCustom.countUsers(form), form.getSize(), page, 2));
        return dto;
    }

    public UserDetailDto getUserDetail(String userId) {
        return usersMapperCustom.selectUserDetail(userId);
    }

    public void create(UserUpsertForm form) {
        UsersExample ex = new UsersExample();
        ex.createCriteria().andEmailEqualTo(form.getEmail());
        // 重複チェック
        if (usersMapper.countByExample(ex) > 0) {
            throw new BusinessException();
        }
        Users u = modelMapper.map(form, Users.class);
        u.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        usersMapper.insertSelective(u);
    }
    
    public List<RoleOptionDto> getRoleOptions(){
        return roleMapperCustom.selectRoleOptions();
    }

}
