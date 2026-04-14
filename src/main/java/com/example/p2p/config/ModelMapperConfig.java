package com.example.p2p.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.p2p.entity.Users;
import com.example.p2p.form.UserUpsertForm;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(UserUpsertForm.class, Users.class).addMappings(m -> {
            m.skip(Users::setPasswordHash);
        });
        return modelMapper;
    }

}
