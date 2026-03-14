package com.example.p2p.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.p2p.entity.User;

import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails {

    private String username;

    private String password;

    private String email;

    public CustomUserDetails(User user) {
        this.username = user.getUserId().toString();
        this.password = user.getPasswordHash();
        this.email = user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO 自動生成されたメソッド・スタブ
        return Collections.EMPTY_LIST;
    }

}
