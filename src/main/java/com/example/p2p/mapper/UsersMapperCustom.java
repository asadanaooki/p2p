package com.example.p2p.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.UserListRowDto;
import com.example.p2p.form.UserSearchForm;

@Mapper
public interface UsersMapperCustom {
    
    List<UserListRowDto> selectUsers(UserSearchForm form);
}
