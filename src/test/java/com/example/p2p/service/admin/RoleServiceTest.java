package com.example.p2p.service.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.Role;
import com.example.p2p.entity.RoleExample;
import com.example.p2p.enums.VisibilityScope;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.admin.RoleUpsertForm;
import com.example.p2p.mapper.RoleMapper;
import com.example.p2p.service.admin.RoleService;

@SpringBootTest
@Transactional
class RoleServiceTest {

    @Autowired
    RoleService roleService;

    @Autowired
    RoleMapper roleMapper;

    @Test
    void update() {
        RoleUpsertForm form = new RoleUpsertForm();
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

        roleService.update("95daf9ce-b599-41e0-ae0d-f4687e718a2c", form);

        Role updated = roleMapper.selectByPrimaryKey("95daf9ce-b599-41e0-ae0d-f4687e718a2c");
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

    @Nested
    class Create {

        @Test
        void create_duplicate() {
            RoleUpsertForm form = new RoleUpsertForm();
            form.setName("管理者");

            assertThatThrownBy(() -> roleService.create(form)).isInstanceOf(BusinessException.class);
        }

        @Test
        void create_success() {
            RoleUpsertForm form = new RoleUpsertForm();
            form.setName("test");
            form.setPrCreate(true);
            form.setPoCreate(true);
            form.setReceiptCreate(false);
            form.setInvoiceCreate(false);
            form.setPrViewScope(VisibilityScope.ALL);
            form.setPoViewScope(VisibilityScope.SELF);
            form.setReceiptViewScope(VisibilityScope.NONE);
            form.setInvoiceViewScope(VisibilityScope.NONE);
            form.setPrApprove(true);
            form.setPoApprove(false);
            form.setInvoiceApprove(false);
            form.setSettingManage(false);
            
            roleService.create(form);
            
            RoleExample ex = new RoleExample();
            ex.createCriteria().andNameEqualTo("test");
           Role created = roleMapper.selectByExample(ex).get(0);
           assertThat(created.getRoleId()).isNotBlank();
           assertThat(created.getName()).isEqualTo("test");
           assertThat(created.getPrCreate()).isTrue();
           assertThat(created.getPoCreate()).isTrue();
           assertThat(created.getReceiptCreate()).isFalse();
           assertThat(created.getInvoiceCreate()).isFalse();
           assertThat(created.getPrViewScope()).isEqualTo(VisibilityScope.ALL.toString());
           assertThat(created.getPoViewScope()).isEqualTo(VisibilityScope.SELF.toString());
           assertThat(created.getReceiptViewScope()).isEqualTo(VisibilityScope.NONE.toString());
           assertThat(created.getInvoiceViewScope()).isEqualTo(VisibilityScope.NONE.toString());
           assertThat(created.getPrApprove()).isTrue();
           assertThat(created.getPoApprove()).isFalse();
           assertThat(created.getInvoiceApprove()).isFalse();
           assertThat(created.getSettingManage()).isFalse();
           assertThat(created.getCreatedAt()).isNotNull();
           assertThat(created.getUpdatedAt()).isNotNull();
        }

    }

}
