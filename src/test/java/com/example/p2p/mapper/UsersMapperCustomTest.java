package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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

import com.example.p2p.dto.ItemListItemDto;
import com.example.p2p.dto.UserListRowDto;
import com.example.p2p.entity.Item;
import com.example.p2p.entity.Supplier;
import com.example.p2p.entity.SupplierExample;
import com.example.p2p.entity.Users;
import com.example.p2p.entity.UsersExample;
import com.example.p2p.enums.ItemKind;
import com.example.p2p.enums.ItemSortBy;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.form.ItemSearchForm;
import com.example.p2p.form.UserSearchForm;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersMapperCustomTest {

    @Autowired
    UsersMapperCustom usersMapperCustom;

    @Autowired
    UsersMapper usersMapper;

    @Nested
    class SelectUsers {

        @Test
        void selectItems_allCondition() {
            UserSearchForm form = new UserSearchForm();
            form.setRoleId("95daf9ce-b599-41e0-ae0d-f4687e718a2c");
            form.setStatus(true);

            List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);

            assertThat(actual).hasSize(1);

            UserListRowDto first = actual.get(0);
            assertThat(first.getUserId()).isEqualTo("be647333-294a-473d-90fa-bc9e62d8a96e");
            assertThat(first.getLastName()).isEqualTo("山田");
            assertThat(first.getFirstName()).isEqualTo("太郎");
            assertThat(first.getEmail()).isEqualTo("siotan0926@gmail.com");
            assertThat(first.getRoleId()).isEqualTo("95daf9ce-b599-41e0-ae0d-f4687e718a2c");
            assertThat(first.getRoleName()).isEqualTo("管理者");
            assertThat(first.isActive()).isTrue();
        }

        @Test
        void selectItems_noCondition() {
            UserSearchForm form = new UserSearchForm();
            List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);
            UserListRowDto first = actual.get(0);
            assertThat(first.getUserId()).isEqualTo("f15fc6b2-af6f-445e-94ef-d1a95788d165");
            assertThat(first.getLastName()).isEqualTo("佐藤");
            assertThat(first.getFirstName()).isEqualTo("花子");
            assertThat(first.getLastNameKana()).isEqualTo("サトウ");
            assertThat(first.getFirstNameKana()).isEqualTo("ハナコ");
            assertThat(first.getEmail()).isEqualTo("sato.hanako@example.com");
            assertThat(first.getRoleId()).isEqualTo("6862542a-1954-4192-81e8-f18c583ade01");
            assertThat(first.getRoleName()).isEqualTo("マネージャー");

        }
        
        @Nested
        class Filter{
            @ParameterizedTest
            @MethodSource("createFilterCaces")
            void selectUsers_filter(UserSearchForm form, int expected) {
                Users u = new Users();
                u.setUserId("5adac01b-123c-4ca7-b7b2-eaad5c1bdc16");
                u.setIsActive(false);
                usersMapper.updateByPrimaryKeySelective(u);
                form.setSize(100);
                
                List<UserListRowDto> actual = usersMapperCustom.selectUsers(form);
                
                assertThat(actual).hasSize(expected);
            }
            
            static Stream<Arguments> createFilterCaces(){
                UserSearchForm roleForm = new UserSearchForm();
                roleForm.setRoleId("71dc166d-5059-4fbc-8bca-71d0c5dc2526");
                UserSearchForm statusForm = new UserSearchForm();
                statusForm.setStatus(false);
                
                return Stream.of(
                        Arguments.of(roleForm, 1),
                        Arguments.of(statusForm, 1)
                        );
            }
            
        }
        
        @Nested
        class Search{
            @ParameterizedTest
            @CsvSource(value = {"やまだ, 3", "文具, 2"})
            void selectItems_singleWord(String keyword, int expected) {
                ItemSearchForm form = new ItemSearchForm();
                form.setKeyword(keyword);
                form.setSize(100);
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);
                
                assertThat(actual).hasSize(expected);
            }
            
            // TODO: 正規化のテスト
            @Nested
            class Normalization {
                @BeforeEach
                void setup() {
                    usersMapper.deleteByExample(new UsersExample());
                    
                    Users u = new Users();
                    u.setUserId("3f7c2a91-5d84-4b6f-9a21-7c8e3f1d6b42");
                    u.setLastName("dummyLastName");
                    u.setFirstName("dummyFirstName");
                    u.setFirstNameKana("dummyFirstNameKana");
                    u.setLastNameKana("dummyLastNameKana");
                    u.setEmail("dummyEmail");
                    u.setPasswordHash("");
                    u.setRoleId("6862542a-1954-4192-81e8-f18c583ade01");

                    usersMapper.insertSelective(u);
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
                @ValueSource(strings = {" ", "　", "(", ")", "[", "]"})
                void selectItems_ignoreSymbol(String symbol) {
                    Supplier s = new Supplier();
                    s.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
                    s.setName("abc" + symbol + "de");
                    supplierMapper.insertSelective(s);
                    
                    Item i = new Item();
                    i.setItemId("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                    i.setName("item2");
                    i.setSupplierId("6b2e9f40-8c17-4ad3-91fe-c2d8a5b7e134");
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
                    itemMapper.insertSelective(i);
                    
                    ItemSearchForm form = new ItemSearchForm();
                    form.setKeyword("asbdgウ漢8字ヲカ%#タ");
                    form.setSize(100);
                    List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);
                    
                    assertThat(actual).hasSize(1);
                    ItemListItemDto dto = actual.get(0);
                    assertThat(dto.getItemId()).isEqualTo("d1a7c5e9-3b64-4f28-a9c6-5e2f8b1d7c90");
                }

            }
            

        }
        
        @Nested 
        class Sort{
            @ParameterizedTest
            @MethodSource("createSortCases")
            void selectItems_sort(ItemSortBy sortBy, SortDirection direction, String includedId) {
                ItemSearchForm form = new ItemSearchForm();
                form.setSortBy(sortBy);
                form.setSortDirection(direction);
                List<ItemListItemDto> actual = itemMapperCustom.selectItems(form);
                
                assertThat(actual).hasSize(2);
                assertThat(actual).extracting(ItemListItemDto::getItemId)
                .contains(includedId);
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
