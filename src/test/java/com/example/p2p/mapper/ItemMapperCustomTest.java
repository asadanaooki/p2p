package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.admin.ItemListItemDto;
import com.example.p2p.dto.app.CatalogListRowDto;
import com.example.p2p.entity.Item;
import com.example.p2p.entity.ItemExample;
import com.example.p2p.entity.PurchaseRequestDetailExample;
import com.example.p2p.entity.PurchaseRequestExample;
import com.example.p2p.entity.Supplier;
import com.example.p2p.entity.SupplierExample;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.ItemSortBy;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.form.admin.ItemSearchForm;
import com.example.p2p.form.app.CatalogSearchForm;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemMapperCustomTest {

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemMapperCustom itemMapperCustom;

    @Autowired
    SupplierMapper supplierMapper;

    @Autowired
    PurchaseRequestMapper purchaseRequestMapper;

    @Autowired
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    @Nested
    class SelectItems {

        @Test
        void selectItems_allCondition() {
            ItemSearchForm form = new ItemSearchForm();
            form.setKind(ItemKind.GOODS);
            form.setPriceMin(10000);
            form.setPriceMax(20000);
            form.setSupplierId("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");
            form.setStatus(true);
            form.setKeyword("液晶");

            List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

            assertThat(actual).hasSize(１);

            ItemListItemDto first = actual.get(0);
            assertThat(first.getItemId()).isEqualTo("f758e462-f526-4b23-a822-8821c5c62adf");
            assertThat(first.getName()).isEqualTo("24インチ液晶モニター");
            assertThat(first.getKind()).isEqualTo(ItemKind.GOODS);
            assertThat(first.getUnitName()).isEqualTo("台");
            assertThat(first.getPrice()).isEqualTo(16800);
            assertThat(first.getSupplierName()).isEqualTo("関西オフィスサービス株式会社");
            assertThat(first.isActive()).isTrue();
        }

        @Test
        void selectItems_noCondition() {
            ItemSearchForm form = new ItemSearchForm();
            form.setPage(2);

            List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

            assertThat(actual).hasSize(2);

            ItemListItemDto first = actual.get(0);
            assertThat(first.getItemId()).isEqualTo("2cc30fd9-9dae-4abe-a145-b280d9de2f38");
            assertThat(first.getName()).isEqualTo("プリンター保守サポート");
            assertThat(first.getKind()).isEqualTo(ItemKind.SERVICE);
            assertThat(first.getUnitName()).isEqualTo("時間");
            assertThat(first.getPrice()).isEqualTo(4500);
            assertThat(first.getSupplierName()).isEqualTo("中部設備サプライ株式会社");
            assertThat(first.isActive()).isFalse();
        }

        @Nested
        class Filter {

            @ParameterizedTest
            @MethodSource("createFilterCaces")
            void selectItems_filter(ItemSearchForm form, int expected) {
                form.setSize(100);

                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).hasSize(expected);
            }

            static Stream<Arguments> createFilterCaces() {
                ItemSearchForm kindForm = new ItemSearchForm();
                kindForm.setKind(ItemKind.GOODS);

                ItemSearchForm priceMinForm = new ItemSearchForm();
                priceMinForm.setPriceMin(4000);

                ItemSearchForm priceMaxForm = new ItemSearchForm();
                priceMaxForm.setPriceMax(10000);

                ItemSearchForm priceRangeForm = new ItemSearchForm();
                priceRangeForm.setPriceMin(14000);
                priceRangeForm.setPriceMax(30000);

                ItemSearchForm supplierForm = new ItemSearchForm();
                supplierForm.setSupplierId("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");

                ItemSearchForm statusForm = new ItemSearchForm();
                statusForm.setStatus(false);

                return Stream.of(Arguments.of(kindForm, 3), Arguments.of(priceMinForm, 3),
                        Arguments.of(priceMaxForm, 3), Arguments.of(priceRangeForm, 2), Arguments.of(supplierForm, 2),
                        Arguments.of(statusForm, 1));
            }

            @ParameterizedTest
            @CsvSource(value = { "680, 4500, 3", "681, 4499, 1" })
            void selectItems_priceFilter_boundary(int priceMin, int priceMax, int expected) {
                ItemSearchForm form = new ItemSearchForm();
                form.setSize(100);
                form.setPriceMin(priceMin);
                form.setPriceMax(priceMax);

                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).hasSize(expected);
            }

        }

        @Nested
        class Search {

            @ParameterizedTest
            @CsvSource(value = { "ター, 3", "文具, 2" })
            void selectItems_singleWord(String keyword, int expected) {
                ItemSearchForm form = new ItemSearchForm();
                form.setKeyword(keyword);
                form.setSize(100);
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).hasSize(expected);
            }

            @Test
            void selectItems_regexWord() {
                Item i = new Item();
                String uuid = UUID.randomUUID().toString();
                i.setItemId(uuid);
                i.setName("test(保守)");
                i.setKind(ItemKind.SERVICE.toString());
                itemMapper.insertSelective(i);

                ItemSearchForm form = new ItemSearchForm();
                form.setKeyword("test保守");

                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).singleElement().extracting(ItemListItemDto::getItemId).isEqualTo(uuid);
            }

            @Test
            void selectItems_multiWord() {
                ItemSearchForm form = new ItemSearchForm();
                form.setKeyword("ボールペン  　黒");
                form.setSize(100);
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).hasSize(1);
                assertThat(actual.get(0).getItemId()).isEqualTo("1bd0d872-69b1-4999-b522-ac202c481662");
            }

            @Nested
            class Normalization {

                @BeforeEach
                void setup() {
                    purchaseRequestDetailMapper.deleteByExample(new PurchaseRequestDetailExample());
                    purchaseRequestMapper.deleteByExample(new PurchaseRequestExample());
                    itemMapper.deleteByExample(new ItemExample());
                    supplierMapper.deleteByExample(new SupplierExample());

                    Supplier s = new Supplier();
                    s.setSupplierId("3f7c2a91-5d84-4b6f-9a21-7c8e3f1d6b42");
                    s.setName("dummySupplier");
                    supplierMapper.insertSelective(s);

                    Item i = new Item();
                    i.setItemId("a8d14c7e-2f93-46b1-b5d8-1e7a9c3f4d65");
                    i.setName("dummyItem");
                    i.setKind(ItemKind.GOODS.toString());
                    itemMapper.insertSelective(i);
                }

                @Test
                void selectItems_toLower() {
                    Supplier s = new Supplier();
                    s.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    s.setName("SUpPlieR2");
                    supplierMapper.insertSelective(s);

                    Item i = new Item();
                    i.setItemId("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                    i.setName("IteM2");
                    i.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    i.setKind(ItemKind.GOODS.toString());
                    itemMapper.insertSelective(i);

                    ItemSearchForm form = new ItemSearchForm();
                    form.setKeyword("tem2");
                    form.setSize(100);
                    List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                    assertThat(actual).hasSize(1);
                    ItemListItemDto dto = actual.get(0);
                    assertThat(dto.getItemId()).isEqualTo("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                }

                @Test
                void selectItems_fullToHalf() {
                    Supplier s = new Supplier();
                    s.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    s.setName("ｖu５カ％");
                    supplierMapper.insertSelective(s);

                    Item i = new Item();
                    i.setItemId("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                    i.setName("IteM2");
                    i.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    i.setKind(ItemKind.GOODS.toString());
                    itemMapper.insertSelective(i);

                    ItemSearchForm form = new ItemSearchForm();
                    form.setKeyword("vu5");
                    form.setSize(100);
                    List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                    assertThat(actual).hasSize(1);
                    ItemListItemDto dto = actual.get(0);
                    assertThat(dto.getItemId()).isEqualTo("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                }

                @Test
                void selectItems_hiraganaToKatakana() {
                    Supplier s = new Supplier();
                    s.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    s.setName("ｖu５カ％");
                    supplierMapper.insertSelective(s);

                    Item i = new Item();
                    i.setItemId("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                    i.setName("たをブ漢");
                    i.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    i.setKind(ItemKind.GOODS.toString());
                    itemMapper.insertSelective(i);

                    ItemSearchForm form = new ItemSearchForm();
                    form.setKeyword("ヲブ 漢");
                    form.setSize(100);
                    List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                    assertThat(actual).hasSize(1);
                    ItemListItemDto dto = actual.get(0);
                    assertThat(dto.getItemId()).isEqualTo("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                }

                @Test
                void selectItems_halfKatakanaToFullKatakana() {
                    Supplier s = new Supplier();
                    s.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    s.setName("ｶあモﾃ");
                    supplierMapper.insertSelective(s);

                    Item i = new Item();
                    i.setItemId("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                    i.setName("item2");
                    i.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    i.setKind(ItemKind.GOODS.toString());
                    itemMapper.insertSelective(i);

                    ItemSearchForm form = new ItemSearchForm();
                    form.setKeyword("カ");
                    form.setSize(100);
                    List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                    assertThat(actual).hasSize(1);
                    ItemListItemDto dto = actual.get(0);
                    assertThat(dto.getItemId()).isEqualTo("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                }

                @ParameterizedTest
                @ValueSource(strings = { " ", "　", "(", ")", "[", "]" })
                void selectItems_ignoreSymbol(String symbol) {
                    Supplier s = new Supplier();
                    s.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    s.setName("abc" + symbol + "de");
                    supplierMapper.insertSelective(s);

                    Item i = new Item();
                    i.setItemId("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                    i.setName("item2");
                    i.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    i.setKind(ItemKind.GOODS.toString());
                    itemMapper.insertSelective(i);

                    ItemSearchForm form = new ItemSearchForm();
                    form.setKeyword("cd");
                    form.setSize(100);
                    List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                    assertThat(actual).hasSize(1);
                    ItemListItemDto dto = actual.get(0);
                    assertThat(dto.getItemId()).isEqualTo("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                }

                @Test
                void selectItems_mixed() {
                    Supplier s = new Supplier();
                    s.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    s.setName("sup");
                    supplierMapper.insertSelective(s);

                    Item i = new Item();
                    i.setItemId("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                    i.setName("　asBDｇ( う漢８字　をカ%#ﾀ    ");
                    i.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    i.setKind(ItemKind.GOODS.toString());
                    itemMapper.insertSelective(i);

                    ItemSearchForm form = new ItemSearchForm();
                    form.setKeyword("asbdgウ漢8字ヲカ%#タ");
                    form.setSize(100);
                    List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                    assertThat(actual).hasSize(1);
                    ItemListItemDto dto = actual.get(0);
                    assertThat(dto.getItemId()).isEqualTo("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                }

                @Test
                void selectItems_multipleHit() {
                    Supplier s = new Supplier();
                    s.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    s.setName("sup");
                    supplierMapper.insertSelective(s);

                    Item i = new Item();
                    i.setName("myI");
                    i.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    i.setKind(ItemKind.GOODS.toString());
                    itemMapper.insertSelective(i);

                    ItemSearchForm form = new ItemSearchForm();
                    form.setKeyword("myi");
                    form.setSize(100);
                    List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                    assertThat(actual).hasSize(2);
                }

            }

        }

        @Nested
        class Sort {

            @ParameterizedTest
            @MethodSource("createSortCases")
            void selectItems_sort(ItemSortBy sortBy, SortDirection direction, String includedId) {
                ItemSearchForm form = new ItemSearchForm();
                form.setSortBy(sortBy);
                form.setSortDirection(direction);
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).hasSize(2);
                assertThat(actual).extracting(ItemListItemDto::getItemId).contains(includedId);
            }

            static Stream<Arguments> createSortCases() {
                return Stream.of(
                        Arguments.of(ItemSortBy.NAME, SortDirection.DESC, "1bd0d872-69b1-4999-b522-ac202c481662"),
                        Arguments.of(ItemSortBy.KIND, SortDirection.ASC, "a5c1b32a-7b01-49d3-8fef-48e0f39dc31f"),
                        Arguments.of(ItemSortBy.UNIT, SortDirection.DESC, "f758e462-f526-4b23-a822-8821c5c62adf"),
                        Arguments.of(ItemSortBy.PRICE, SortDirection.ASC, "a5c1b32a-7b01-49d3-8fef-48e0f39dc31f"),
                        Arguments.of(ItemSortBy.SUPPLIER, SortDirection.DESC, "2cc30fd9-9dae-4abe-a145-b280d9de2f38"),
                        Arguments.of(ItemSortBy.STATUS, SortDirection.ASC, "2cc30fd9-9dae-4abe-a145-b280d9de2f38"));
            }

        }

        @Nested
        class PairWise {

            @Test
            void selectItems_keywordAndsupplier() {
                ItemSearchForm form = new ItemSearchForm();
                form.setKeyword("液晶");
                form.setSupplierId("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f");

                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).singleElement()
                    .extracting(ItemListItemDto::getItemId)
                    .isEqualTo("f758e462-f526-4b23-a822-8821c5c62adf");
            }

            @Test
            void selectItems_kindAndStatus() {
                ItemSearchForm form = new ItemSearchForm();
                form.setKind(ItemKind.SERVICE);
                form.setStatus(true);

                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).extracting(ItemListItemDto::getItemId)
                    .containsExactlyInAnyOrder("d21fb363-afb3-4911-a051-fac108a06658");
            }

        }

    }

    @Nested
    class SelectCatalogItems {

        @Test
        void selectItems_allCondition() {
            CatalogSearchForm form = new CatalogSearchForm();
            form.setKind(ItemKind.SERVICE);
            form.setSupplierId("c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a");
            form.setKeyword("プリンタ");
            Item i = new Item();
            i.setItemId("2cc30fd9-9dae-4abe-a145-b280d9de2f38");
            i.setIsActive(true);
            itemMapper.updateByPrimaryKeySelective(i);

            List<CatalogListRowDto> actual = itemMapperCustom.selectCatalogItems(form);

            assertThat(actual).hasSize(１);

            CatalogListRowDto first = actual.get(0);
            assertThat(first.getItemId()).isEqualTo("2cc30fd9-9dae-4abe-a145-b280d9de2f38");
            assertThat(first.getItemName()).isEqualTo("プリンター保守サポート");
            assertThat(first.getKind()).isEqualTo(ItemKind.SERVICE);
            assertThat(first.getSupplierId()).isEqualTo("c9e2a4b6-7d1f-43a8-b5c2-9f0e1d2c3b4a");
            assertThat(first.getSupplierName()).isEqualTo("中部設備サプライ株式会社");
            assertThat(first.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222225");
            assertThat(first.getUnitName()).isEqualTo("時間");
            assertThat(first.getPrice()).isEqualTo(4500);
        }

        @Test
        void selectCatalogItems_noCondition() {
            CatalogSearchForm form = new CatalogSearchForm();
            form.setPage(2);

            List<CatalogListRowDto> actual = itemMapperCustom.selectCatalogItems(form);

            assertThat(actual).hasSize(2);

            CatalogListRowDto first = actual.get(0);
            assertThat(first.getItemId()).isEqualTo("1bd0d872-69b1-4999-b522-ac202c481662");
            assertThat(first.getItemName()).isEqualTo("油性ボールペン 黒 10本セット");
            assertThat(first.getKind()).isEqualTo(ItemKind.GOODS);
            assertThat(first.getSupplierId()).isEqualTo("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            assertThat(first.getSupplierName()).isEqualTo("神奈川文具株式会社");
            assertThat(first.getUnitId()).isEqualTo("22222222-2222-2222-2222-222222222221");
            assertThat(first.getUnitName()).isEqualTo("個");
            assertThat(first.getPrice()).isEqualTo(980);
        }

        @Nested
        class Filter {

            @ParameterizedTest
            @MethodSource("createFilterCaces")
            void selectCatalogItems_filter(Consumer<CatalogSearchForm> consumer, int expected) {
                CatalogSearchForm form = new CatalogSearchForm();
                form.setSize(100);
                consumer.accept(form);

                List<CatalogListRowDto> actual = itemMapperCustom.selectCatalogItems(form);

                assertThat(actual).hasSize(expected);
            }

            static Stream<Arguments> createFilterCaces() {
                return Stream
                    .of(Arguments.of((Consumer<CatalogSearchForm>) f -> f.setKind(ItemKind.GOODS), 3), Arguments.of(
                            (Consumer<CatalogSearchForm>) f -> f.setSupplierId("b4d8e1f7-92ac-4c35-8f21-6a7b8c9d0e1f"),
                            2));
            }

        }

        @Nested
        class Search {

            @Test
            void selectCatalogItems_singleWord() {
                CatalogSearchForm form = new CatalogSearchForm();
                form.setKeyword("ター");
                form.setSize(100);
                List<CatalogListRowDto> actual = itemMapperCustom.selectCatalogItems(form);

                assertThat(actual).hasSize(2);
            }

            @Test
            void selectCatalogItems_multiWords() {
                ItemSearchForm form = new ItemSearchForm();
                form.setKeyword("ボールペン  　黒");
                form.setSize(100);
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);

                assertThat(actual).hasSize(1);
                assertThat(actual.get(0).getItemId()).isEqualTo("1bd0d872-69b1-4999-b522-ac202c481662");
            }

        }

    }

}
