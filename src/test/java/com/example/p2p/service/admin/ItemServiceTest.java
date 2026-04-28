package com.example.p2p.service.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.admin.ItemEditViewDto;
import com.example.p2p.dto.admin.ItemListViewDto;
import com.example.p2p.entity.Item;
import com.example.p2p.entity.ItemExample;
import com.example.p2p.entity.PurchaseRequestDetailExample;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.form.admin.ItemSearchForm;
import com.example.p2p.form.admin.ItemUpsertForm;
import com.example.p2p.mapper.ItemMapper;
import com.example.p2p.mapper.ItemMapperCustom;
import com.example.p2p.mapper.PurchaseRequestDetailMapper;
import com.example.p2p.service.admin.ItemService;
import com.example.p2p.util.CommonUtil;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemMapperCustom itemMapperCustom;

    @Autowired
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;
    

    @Nested
    class SearchItems {

        @BeforeEach
        void setup() {
            purchaseRequestDetailMapper.deleteByExample(new PurchaseRequestDetailExample());
            itemMapper.deleteByExample(new ItemExample());
        }

        @ParameterizedTest
        @MethodSource("createPaginationCases")
        void searchItems_exists(int page, int expItemcount, List<Integer> pageNumbers) {
            StringBuilder sb = new StringBuilder();
            sb.append("0".repeat(8));
            sb.append("-");
            sb.append("0".repeat(4));
            sb.append("-");
            sb.append("0".repeat(4));
            sb.append("-");
            sb.append("0".repeat(4));
            sb.append("-");
            sb.append("0".repeat(12));
            String firstUUID = sb.toString();

            for (int i = 0; i < 50; i++) {
                Item item = new Item();
                String replace = String.format("%02d", i);
                String uuid = firstUUID.substring(firstUUID.length() - 2) + replace;
                String name = "test-" + i;

                item.setUnitId(uuid);
                item.setName(name);
                item.setKind(ItemKind.GOODS.toString());
                item.setUnitId("22222222-2222-2222-2222-222222222221");
                item.setPrice(1200);
                item.setSupplierId("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
                itemMapper.insertSelective(item);
            }

            ItemSearchForm form = new ItemSearchForm();
            form.setPage(page);
            form.setSize(7);
            ItemListViewDto actual = itemService.searchItems(form);
            
            assertThat(actual.getItems()).hasSize(expItemcount);
            assertThat(actual.getPageNumberList()).isEqualTo(pageNumbers);
            assertThat(actual.getCurrentPage()).isEqualTo(page);

        }

        static Stream<Arguments> createPaginationCases() {
            // 8ページ作成
            return Stream.of(
                    Arguments.of(1, 7, CommonUtil.createPageNumbers(50, 7, 1, 2)),
                    Arguments.of(2, 7, CommonUtil.createPageNumbers(50, 7, 2, 2)),
                    Arguments.of(3, 7, CommonUtil.createPageNumbers(50, 7, 3, 2)),
                    Arguments.of(4, 7, CommonUtil.createPageNumbers(50, 7, 4, 2)),
                    Arguments.of(5, 7, CommonUtil.createPageNumbers(50, 7, 5, 2)),
                    Arguments.of(6, 7, CommonUtil.createPageNumbers(50, 7, 6, 2)),
                    Arguments.of(7, 7, CommonUtil.createPageNumbers(50, 7, 7, 2)),
                    Arguments.of(8, 1, CommonUtil.createPageNumbers(50, 7, 8, 2)));
        }

        @Test
        void searchItems_notFound() {
            ItemListViewDto actual = itemService.searchItems(new ItemSearchForm());

            assertThat(actual.getItems()).isEmpty();
            assertThat(actual.getCurrentPage()).isEqualTo(1);
            assertThat(actual.getPageNumberList()).isEmpty();
        }

    }

    @Test
    void update() {
        ItemUpsertForm form = new ItemUpsertForm();
        form.setName("test");
        form.setKind(ItemKind.SERVICE);
        form.setUnitId("22222222-2222-2222-2222-222222222222");
        form.setPrice(1);
        form.setSupplierId("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
        form.setDescription("testdescription");
        form.setActive(false);
        
        itemService.update("a5c1b32a-7b01-49d3-8fef-48e0f39dc31f", form);
        
       Item updated = itemMapper.selectByPrimaryKey("a5c1b32a-7b01-49d3-8fef-48e0f39dc31f");
       
       assertThat(updated.getName()).isEqualTo("test");
       assertThat(updated.getKind()).isEqualTo(ItemKind.SERVICE.toString());
       assertThat(updated.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222222");
       assertThat(updated.getPrice()).isEqualTo(1);
       assertThat(updated.getSupplierId()).isEqualTo("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
       assertThat(updated.getDescription()).isEqualTo("testdescription");
       assertThat(updated.getIsActive()).isFalse();
       assertThat(updated.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.of(2026, 3, 24));
       assertThat(updated.getUpdatedAt().toLocalDate()).isEqualTo(LocalDate.now());
    }
    
    @Test
    void prepareItemEditView() {
       ItemEditViewDto actual = itemService.prepareItemEditView("a5c1b32a-7b01-49d3-8fef-48e0f39dc31f");
       
       assertThat(actual.getName()).isEqualTo("A4コピー用紙 500枚");
       assertThat(actual.getKind()).isEqualTo(ItemKind.GOODS);
       assertThat(actual.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222221");
       assertThat(actual.getPrice()).isEqualTo(680);
       assertThat(actual.getSupplierId()).isEqualTo("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
       assertThat(actual.getDescription()).isEqualTo("社内文書や見積書の印刷に使用する標準的なA4コピー用紙です。");
       assertThat(actual.isActive()).isTrue();
    }
    
    @Test
    void create() {
        ItemUpsertForm form = new ItemUpsertForm();
        form.setName("test");
        form.setKind(ItemKind.SERVICE);
        form.setUnitId("22222222-2222-2222-2222-222222222222");
        form.setPrice(1300);
        form.setSupplierId("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
        form.setDescription("testDesc");
        
        itemService.create(form);
        
        ItemExample ex = new ItemExample();
        ex.createCriteria().andNameEqualTo("test");
       Item created = itemMapper.selectByExample(ex).get(0);
       
       assertThat(created.getItemId()).isNotBlank();
       assertThat(created.getName()).isEqualTo("test");
       assertThat(created.getKind()).isEqualTo(ItemKind.SERVICE.toString());
       assertThat(created.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222222");
       assertThat(created.getPrice()).isEqualTo(1300);
       assertThat(created.getSupplierId()).isEqualTo("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
       assertThat(created.getDescription()).isEqualTo("testDesc");
       assertThat(created.getIsActive()).isTrue();
       assertThat(created.getCreatedAt()).isNotNull();
       assertThat(created.getUpdatedAt()).isNotNull();
    }
}
