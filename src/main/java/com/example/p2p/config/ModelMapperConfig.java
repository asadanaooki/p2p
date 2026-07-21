package com.example.p2p.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.p2p.entity.Users;
import com.example.p2p.form.admin.UserUpsertForm;
import com.example.p2p.form.app.PurchaseOrderCreatePreparationForm;
import com.example.p2p.session.app.PurchaseOrderCreateDraft;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(UserUpsertForm.class, Users.class).addMappings(m -> {
            m.skip(Users::setUserId);
            m.skip(Users::setPasswordHash);
        });
        modelMapper.typeMap(PurchaseOrderCreatePreparationForm.class, PurchaseOrderCreateDraft.class).addMappings(m -> {
            m.skip(PurchaseOrderCreateDraft::setDetails);
        });
        return modelMapper;
    }

}
