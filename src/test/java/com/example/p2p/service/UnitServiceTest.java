package com.example.p2p.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.entity.Unit;
import com.example.p2p.entity.UnitExample;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UnitEditForm;
import com.example.p2p.mapper.UnitMapper;

@SpringBootTest
@Transactional
class UnitServiceTest {

    @Autowired
    UnitService unitService;

    @Autowired
    UnitMapper unitMapper;

    @Nested
    class create {

        @Test
        void create_duplicate() {
            String name = "個";
            UnitExample e = new UnitExample();
            e.createCriteria().andNameEqualTo(name);

            assertThatThrownBy(() -> unitService.create(name)).isInstanceOf(BusinessException.class);
        }

        @Test
        void create_success() {
            String name = "テスト";
            unitService.create(name);

            UnitExample e = new UnitExample();
            e.createCriteria().andNameEqualTo(name);
            List<Unit> actual = unitMapper.selectByExample(e);

            assertThat(actual).hasSize(1);
            Unit unit = actual.get(0);
            assertThat(unit.getUnitId()).isNotNull();
            assertThat(unit.getName()).isEqualTo(name.strip());
            assertThat(unit.getIsActive()).isTrue();
            assertThat(unit.getCreatedAt()).isNotNull();
            assertThat(unit.getUpdatedAt()).isNotNull();
        }

    }

    @Nested
    class update {

        String unitId = "3b5928ee-6e20-4afb-915b-6540f80f3bef";

        @Test
        void update_duplicate() {
            UnitEditForm form = new UnitEditForm(" 個", true);

            assertThatThrownBy(() -> unitService.update(unitId, form)).isInstanceOf(BusinessException.class);
        }

        @Test
        void update_status() {
            LocalDateTime now = LocalDateTime.now();

            UnitEditForm form = new UnitEditForm("時間", false);
            unitService.update(unitId, form);

            Unit unit = unitMapper.selectByPrimaryKey(unitId);

            assertThat(unit.getName()).isEqualTo("時間");
            assertThat(unit.getIsActive()).isFalse();
            assertThat(unit.getUpdatedAt()).isAfter(now);
        }

        @Test
        void update_name() {
            LocalDateTime beforeUpdate = LocalDateTime.now();

            UnitEditForm form = new UnitEditForm("test", true);
            unitService.update(unitId, form);

            Unit unit = unitMapper.selectByPrimaryKey(unitId);

            assertThat(unit.getName()).isEqualTo("test");
            assertThat(unit.getIsActive()).isTrue();
            assertThat(unit.getUpdatedAt()).isAfter(beforeUpdate);
        }

    }

}
