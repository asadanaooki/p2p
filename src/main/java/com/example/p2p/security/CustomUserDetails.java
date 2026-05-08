package com.example.p2p.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails {

    private String username;

    private String password;

    private String email;

    private List<SimpleGrantedAuthority> authorities;

    public CustomUserDetails(String userId, String passwordHash, String email, String role) {
        this.username = userId;
        this.password = passwordHash;
        this.email = email;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

}
