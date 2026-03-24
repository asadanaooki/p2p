package com.example.p2p.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.intThat;

import java.rmi.server.UnicastRemoteObject;
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

import com.example.p2p.dto.ItemListViewDto;
import com.example.p2p.entity.Item;
import com.example.p2p.entity.ItemExample;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.mapper.ItemMapper;
import com.example.p2p.mapper.ItemMapperCustom;
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

    @Nested
    class SearchItems {

        @BeforeEach
        void setup() {
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
            
            var a = itemMapper.selectByExample(new ItemExample());

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

}
