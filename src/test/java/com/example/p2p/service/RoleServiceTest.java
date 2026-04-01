package com.example.p2p.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.Role;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.form.RoleEditForm;
import com.example.p2p.mapper.RoleMapper;

@SpringBootTest
@Transactional
class RoleServiceTest {

    @Autowired
    RoleService roleService;
    
    @Autowired
    RoleMapper roleMapper;
    
    @Test
    void update() {
        RoleEditForm form = new RoleEditForm();
        form.setName("管理者");
        form.setPrCreate(false);
        form.setPoCreate(false);
        form.setReceiptCreate(false);
        form.setInvoiceCreate(false);
        form.setPrViewScope(VisibilityScope.SELF);
        form.setPoViewScope(VisibilityScope.NONE);
        form.setReceiptViewScope(VisibilityScope.SELF);
        form.setInvoiceViewScope(VisibilityScope.NONE);
        form.setPrApprove(false);
        form.setPoApprove(false);
        form.setInvoiceApprove(false);
        form.setSettingManage(false);
        
        roleService.update("97cc088f-bcad-46ad-b66e-a15a4c4ac7ea", form);
        
       Role updated = roleMapper.selectByPrimaryKey("97cc088f-bcad-46ad-b66e-a15a4c4ac7ea");
       assertThat(updated.getName()).isEqualTo("管理者");
       assertThat(updated.getPrCreate()).isFalse();
       assertThat(updated.getPoCreate()).isFalse();
       assertThat(updated.getReceiptCreate()).isFalse();
       assertThat(updated.getInvoiceCreate()).isFalse();
       assertThat(updated.getPrViewScope()).isEqualTo(VisibilityScope.SELF.toString());
       assertThat(updated.getPoViewScope()).isEqualTo(VisibilityScope.NONE.toString());
       assertThat(updated.getReceiptViewScope()).isEqualTo(VisibilityScope.SELF.toString());
       assertThat(updated.getInvoiceViewScope()).isEqualTo(VisibilityScope.NONE.toString());
       assertThat(updated.getPrApprove()).isFalse();
       assertThat(updated.getPoApprove()).isFalse();
       assertThat(updated.getInvoiceApprove()).isFalse();
       assertThat(updated.getSettingManage()).isFalse();
       assertThat(updated.getUpdatedAt().toLocalDate()).isEqualTo(LocalDate.now());
    }
}
