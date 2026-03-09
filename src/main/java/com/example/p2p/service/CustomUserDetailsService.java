package com.example.p2p.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.p2p.entity.User;
import com.example.p2p.mapper.UserMapper;
import com.example.p2p.mapper.UserMapperCustom;
import com.example.p2p.security.CustomUserDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserMapperCustom userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userMapper.selectByEmail(username.strip().toLowerCase());
        if (u == null) {
            throw new UsernameNotFoundException("not found");
        }
        return new CustomUserDetails(u);
    }

}
