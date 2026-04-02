package com.example.p2p.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.p2p.service.RoleService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class RoleControllerTest {

    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    RoleService roleService;
}
