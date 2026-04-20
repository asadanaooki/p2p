package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.admin.RoleDetailDto;
import com.example.p2p.enums.VisibilityScope;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleMapperCustomTest {

    @Autowired
    RoleMapperCustom roleMapperCustom;
    
    @Test
    void selectRoleDetail() {
        RoleDetailDto actual = roleMapperCustom.selectRoleDetail("6862542a-1954-4192-81e8-f18c583ade01");
        
        assertThat(actual.getName()).isEqualTo("マネージャー");
        assertThat(actual.isPrCreate()).isTrue();
        assertThat(actual.isPoCreate()).isTrue();
        assertThat(actual.isReceiptCreate()).isTrue();
        assertThat(actual.isInvoiceCreate()).isTrue();
        assertThat(actual.getPrViewScope()).isEqualTo(VisibilityScope.ALL);
        assertThat(actual.getPoViewScope()).isEqualTo(VisibilityScope.ALL);
        assertThat(actual.getReceiptViewScope()).isEqualTo(VisibilityScope.ALL);
        assertThat(actual.getInvoiceViewScope()).isEqualTo(VisibilityScope.ALL);
        assertThat(actual.isPrApprove()).isTrue();
        assertThat(actual.isPoApprove()).isTrue();
        assertThat(actual.isInvoiceApprove()).isTrue();
        assertThat(actual.isSettingManage()).isFalse();
    }
}
