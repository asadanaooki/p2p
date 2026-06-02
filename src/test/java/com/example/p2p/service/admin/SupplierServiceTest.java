package com.example.p2p.service.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.admin.SupplierListItemDto;
import com.example.p2p.entity.Supplier;
import com.example.p2p.entity.SupplierExample;
import com.example.p2p.form.admin.SupplierCreateForm;
import com.example.p2p.form.admin.SupplierEditForm;
import com.example.p2p.mapper.SupplierMapper;

@SpringBootTest
@Transactional
class SupplierServiceTest {

    @Autowired
    SupplierService supplierService;

    @Autowired
    SupplierMapper supplierMapper;

    @Nested
    class GetSuppliers {

        @ParameterizedTest
        @EmptySource
        @NullSource
        void getSuppliers_noCondition(String name) {
            List<SupplierListItemDto> actual = supplierService.getSuppliers(null, name);

            assertThat(actual).hasSize(3);
            assertThat(actual).extracting(SupplierListItemDto::getSupplierId)
                .containsExactlyInAnyOrder("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f", "a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c",
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
        void getSuppliers_byStatus() {
            List<SupplierListItemDto> actual = supplierService.getSuppliers(false, "");

            assertThat(actual).extracting(SupplierListItemDto::getSupplierId)
                .containsExactly("c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a");
        }

        @Test
        void getSuppliers_byKeyword() {
            List<SupplierListItemDto> actual = supplierService.getSuppliers(null, "文具");

            assertThat(actual).extracting(SupplierListItemDto::getSupplierId)
                .containsExactly("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
        }

    }

    @Nested
    class Update {

        @Test
        void update_allFields() {
            SupplierEditForm form = new SupplierEditForm();
            form.setName("test");
            form.setEmail("test@example.com");
            form.setPhoneNumber("0743712224");
            form.setPostalCode("6312214");
            form.setPrefecture("青森県");
            form.setCity("青森市");
            form.setStreetAddress("2-3-4");
            form.setBuildingName("テストビル");
            form.setPaymentTermId("33333333-3333-3333-3333-333333333332");
            form.setStatus(false);

            supplierService.update("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f", form);

            Supplier updated = supplierMapper.selectByPrimaryKey("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
            assertThat(updated.getName()).isEqualTo("test");
            assertThat(updated.getEmail()).isEqualTo("test@example.com");
            assertThat(updated.getPhoneNumber()).isEqualTo("0743712224");
            assertThat(updated.getPostalCode()).isEqualTo("6312214");
            assertThat(updated.getPrefecture()).isEqualTo("青森県");
            assertThat(updated.getCity()).isEqualTo("青森市");
            assertThat(updated.getStreetAddress()).isEqualTo("2-3-4");
            assertThat(updated.getPaymentTermId()).isEqualTo("33333333-3333-3333-3333-333333333332");
            assertThat(updated.getBuildingName()).isEqualTo("テストビル");
            assertThat(updated.getIsActive()).isFalse();
        }

        @Test
        void update_optionalFieldsNotSet() {
            SupplierEditForm form = new SupplierEditForm();
            form.setName("test");
            form.setEmail("test@example.com");
            form.setStatus(false);

            supplierService.update("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f", form);

            Supplier updated = supplierMapper.selectByPrimaryKey("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
            assertThat(updated.getName()).isEqualTo("test");
            assertThat(updated.getEmail()).isEqualTo("test@example.com");
            assertThat(updated.getPhoneNumber()).isNull();
            assertThat(updated.getPostalCode()).isNull();
            assertThat(updated.getPrefecture()).isNull();
            assertThat(updated.getCity()).isNull();
            assertThat(updated.getStreetAddress()).isNull();
            assertThat(updated.getPaymentTermId()).isNull();
            assertThat(updated.getBuildingName()).isNull();
            assertThat(updated.getIsActive()).isFalse();
        }

    }

    @Nested
    class Create {

        @Test
        void create_allFields() {
            SupplierCreateForm form = new SupplierCreateForm();
            form.setName("test1234");
            form.setEmail("test@example.com");
            form.setPhoneNumber("0743712224");
            form.setPostalCode("6312214");
            form.setPrefecture("青森県");
            form.setCity("青森市");
            form.setStreetAddress("2-3-4");
            form.setBuildingName("テストビル");
            form.setPaymentTermId("33333333-3333-3333-3333-333333333332");

            supplierService.create(form);

            SupplierExample ex = new SupplierExample();
            ex.createCriteria().andNameEqualTo("test1234");
            Supplier created = supplierMapper.selectByExample(ex).get(0);

            assertThat(created.getSupplierId()).isNotBlank();
            assertThat(created.getName()).isEqualTo("test1234");
            assertThat(created.getEmail()).isEqualTo("test@example.com");
            assertThat(created.getPhoneNumber()).isEqualTo("0743712224");
            assertThat(created.getPostalCode()).isEqualTo("6312214");
            assertThat(created.getPrefecture()).isEqualTo("青森県");
            assertThat(created.getCity()).isEqualTo("青森市");
            assertThat(created.getStreetAddress()).isEqualTo("2-3-4");
            assertThat(created.getBuildingName()).isEqualTo("テストビル");
            assertThat(created.getPaymentTermId()).isEqualTo("33333333-3333-3333-3333-333333333332");
            assertThat(created.getIsActive()).isTrue();
            assertThat(created.getCreatedAt()).isNotNull();
            assertThat(created.getUpdatedAt()).isNotNull();

        }

        @Test
        void create_onlyRequiredField() {
            SupplierCreateForm form = new SupplierCreateForm();
            form.setName("test1234");

            supplierService.create(form);

            SupplierExample ex = new SupplierExample();
            ex.createCriteria().andNameEqualTo("test1234");
            Supplier created = supplierMapper.selectByExample(ex).get(0);

            assertThat(created.getSupplierId()).isNotBlank();
            assertThat(created.getName()).isEqualTo("test1234");
            assertThat(created.getEmail()).isNull();
            assertThat(created.getPhoneNumber()).isNull();
            assertThat(created.getPostalCode()).isNull();
            assertThat(created.getPrefecture()).isNull();
            assertThat(created.getCity()).isNull();
            assertThat(created.getStreetAddress()).isNull();
            assertThat(created.getBuildingName()).isNull();
            assertThat(created.getPaymentTermId()).isNull();
            assertThat(created.getIsActive()).isTrue();
            assertThat(created.getCreatedAt()).isNotNull();
            assertThat(created.getUpdatedAt()).isNotNull();
        }

    }

}
