package com.example.p2p.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.p2p.entity.User;
import com.example.p2p.entity.UserExample;
import com.example.p2p.mapper.UserMapper;
import com.example.p2p.security.CustomUserDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserExample ex = new UserExample();
        ex.createCriteria().andEmailEqualTo(username.strip().toLowerCase());
        List<User> u = userMapper.selectByExample(ex);
        if (u.isEmpty()) {
            throw new UsernameNotFoundException("not found");
        }
        return new CustomUserDetails(u.get(0));
    }

}
