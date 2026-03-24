package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.p2p.dto.ItemListItemDto;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.ItemSortBy;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.form.ItemSearchForm;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemMapperCustomTest {

    @Autowired
    ItemMapperCustom itemMapperCustom;
    
    @Nested
    class SelectItems{
        @Test
        void selectItems_allCondition(){
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
        void selectItems_noCondition(){
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
        class Filter{
            @ParameterizedTest
            @MethodSource("createFilterCaces")
            void selectItems_filter(ItemSearchForm form, int expected) {
                form.setSize(100);
                
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);
                
                assertThat(actual).hasSize(expected);
            }
            
            static Stream<Arguments> createFilterCaces(){
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
                
                return Stream.of(
                        Arguments.of(kindForm, 3),
                        Arguments.of(priceMinForm, 3),
                        Arguments.of(priceMaxForm, 3),
                        Arguments.of(priceRangeForm, 2),
                        Arguments.of(supplierForm, 2),
                        Arguments.of(statusForm, 1)
                        );
            }
            
            @ParameterizedTest
            @CsvSource(value = {"680, 4500, 3", "681, 4499, 1"})
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
        class Search{
            @ParameterizedTest
            @CsvSource(value = {"ター, 3", "文具, 2"})
            void selectItems_singleWord(String keyword, int expected) {
                ItemSearchForm form = new ItemSearchForm();
                form.setKeyword(keyword);
                form.setSize(100);
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);
                
                assertThat(actual).hasSize(expected);
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
        }
        
        @Nested 
        class Sort{
            @ParameterizedTest
            @MethodSource("createSortCases")
            void selectItems_sort(ItemSortBy sortBy, SortDirection direction, String firstElementId) {
                ItemSearchForm form = new ItemSearchForm();
                form.setSortBy(sortBy);
                form.setSortDirection(direction);
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);
                
                assertThat(actual).hasSize(2);
                assertThat(actual.get(0).getItemId()).isEqualTo(firstElementId);
            }
            
            static Stream<Arguments> createSortCases(){
                return Stream.of(
                        Arguments.of(ItemSortBy.NAME, SortDirection.DESC, "1bd0d872-69b1-4999-b522-ac202c481662"),
                        Arguments.of(ItemSortBy.KIND, SortDirection.ASC, "a5c1b32a-7b01-49d3-8fef-48e0f39dc31f"),
                        Arguments.of(ItemSortBy.UNIT, SortDirection.DESC, "f758e462-f526-4b23-a822-8821c5c62adf"),
                        Arguments.of(ItemSortBy.PRICE, SortDirection.ASC, "a5c1b32a-7b01-49d3-8fef-48e0f39dc31f"),
                        Arguments.of(ItemSortBy.SUPPLIER, SortDirection.DESC, "2cc30fd9-9dae-4abe-a145-b280d9de2f38"),
                        Arguments.of(ItemSortBy.STATUS, SortDirection.ASC, "2cc30fd9-9dae-4abe-a145-b280d9de2f38")
                        );
            }
        }
        
        @Nested
        class PairWise{
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
                .containsExactlyInAnyOrder(
                        "d21fb363-afb3-4911-a051-fac108a06658"
                        );
            }
        }
    }
}
