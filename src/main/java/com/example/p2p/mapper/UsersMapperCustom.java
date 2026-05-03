package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.admin.UserDetailDto;
import com.example.p2p.dto.admin.UserListRowDto;
import com.example.p2p.dto.app.UserProfileDto;
import com.example.p2p.form.admin.UserSearchForm;

@Mapper
public interface UsersMapperCustom {
    
    List<UserListRowDto> selectUsers(UserSearchForm form);
    
    int countUsers(UserSearchForm form);
    
    UserDetailDto selectUserDetail(String userId);
    
    UserProfileDto selectUserProfile(String userId);
    
    String selectFullName(String userId);
}
