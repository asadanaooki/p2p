package com.example.p2p.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.entity.User;

@Mapper
public interface UserMapperCustom extends UserMapper {

    User selectByEmail(String email);
}
