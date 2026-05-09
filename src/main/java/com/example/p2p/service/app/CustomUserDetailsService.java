package com.example.p2p.service.app;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.app.AuthenticationUserDto;
import com.example.p2p.mapper.UsersMapperCustom;
import com.example.p2p.security.CustomUserDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UsersMapperCustom usersMapperCustom;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthenticationUserDto user = usersMapperCustom.selectAuthenticationUser(username.strip().toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("not found");
        }
        return new CustomUserDetails(user);
    }

}
