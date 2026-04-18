package com.example.p2p.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.p2p.dto.InitialPasswordSetupViewDto;

@Mapper
public interface UserInvitationTokenMapperCustom {
    
    InitialPasswordSetupViewDto selectUserNameAndEmail(String tokenHash);
}
