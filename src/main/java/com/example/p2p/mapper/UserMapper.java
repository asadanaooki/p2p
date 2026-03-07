package com.example.p2p.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.entity.User;

@Mapper
public interface UserMapper {

    User selectByEmail(String email);
}
