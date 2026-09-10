package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.admin.SupplierDetailDto;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
class SupplierMapperCustomTest {

    @Autowired
    SupplierMapperCustom supplierMapperCustom;
    
    @Test
    void selectSupplierDetail() {
        SupplierDetailDto detail = supplierMapperCustom.selectSupplierDetail("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
        
        assertThat(detail.getSupplierName()).isEqualTo("関西オフィスサービス株式会社");
        assertThat(detail.getSupplierNameKana()).isEqualTo("カンサイオフィスサービスカブシキガイシャ");
        assertThat(detail.getEmail()).isEqualTo("contact@kansai-office.jp");
        assertThat(detail.getPhoneNumber()).isEqualTo("0661234567");
        assertThat(detail.getPostalCode()).isEqualTo("5400008");
        assertThat(detail.getPrefecture()).isEqualTo("大阪府");
        assertThat(detail.getCity()).isEqualTo("大阪市中央区");
        assertThat(detail.getStreetAddress()).isEqualTo("北浜1-8-16");
        assertThat(detail.getBuildingName()).isEqualTo("北浜オフィスタワー 8F");
        assertThat(detail.getPaymentTermId()).isEqualTo("33333333-3333-3333-3333-333333333333");
        assertThat(detail.getPaymentTermName()).isEqualTo("今月末払い");
        assertThat(detail.isActive()).isTrue();
        
    }
}
