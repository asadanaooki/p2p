package com.example.p2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.p2p.dto.UserDetailDto;
import com.example.p2p.dto.UserListRowDto;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.mapper.UsersMapperCustom;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private UsersMapperCustom usersMapperCustom;
    
    public List<UserListRowDto> searchUsers(UserSearchForm form){
        return usersMapperCustom.selectUsers(form);
    }
    
    public UserDetailDto getUserDetail(String userId) {
        return usersMapperCustom.selectUserDetail(userId);
    }
}
