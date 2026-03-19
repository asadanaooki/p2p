package com.example.p2p.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.SupplierListItemDto;
import com.example.p2p.mapper.SupplierMapper;

@SpringBootTest
@Transactional
class SupplierServiceTest {

    @Autowired
    SupplierService supplierService;

    @Autowired
    SupplierMapper supplierMapper;
    
    
    @Nested
    class GetSuppliers{
        @ParameterizedTest
        @EmptySource
        @NullSource
        void getSuppliers_noCondition(String name) {
           List<SupplierListItemDto> actual = supplierService.getSuppliers(null, name);
           
           assertThat(actual).hasSize(3);
           assertThat(actual).extracting(SupplierListItemDto::getSupplierId)
           .containsExactly("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f",
                   "a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c",
                   "c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a");
        }
        
        @Test
        void getSuppliers_allCondition() {
            List<SupplierListItemDto> actual = supplierService.getSuppliers(true, "オフィス");
            
            assertThat(actual).singleElement()
            .extracting(SupplierListItemDto::getSupplierId)
            .isEqualTo("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
        }
        
        @Test
        void getSuppliers_status() {
            List<SupplierListItemDto> actual = supplierService.getSuppliers(false, "");
            
            assertThat(actual).extracting(SupplierListItemDto::getSupplierId)
            .containsExactly("c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a");
        }
        
        @Test
        void getSuppliers_name() {
            List<SupplierListItemDto> actual = supplierService.getSuppliers(null, "文具");
            
            assertThat(actual).extracting(SupplierListItemDto::getSupplierId)
            .containsExactly("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
        }
    }
    
}
