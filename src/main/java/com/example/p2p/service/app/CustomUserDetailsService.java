package com.example.p2p.service.app;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.p2p.entity.Users;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.mapper.UsersMapper;
import com.example.p2p.security.CustomUserDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UsersMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersExample ex = new UsersExample();
        ex.createCriteria().andEmailEqualTo(username.strip().toLowerCase());
        List<Users> u = userMapper.selectByExample(ex);
        if (u.isEmpty()) {
            throw new UsernameNotFoundException("not found");
        }
        return new CustomUserDetails(u.get(0));
    }

}
