package com.example.p2p.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
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

import com.example.p2p.dto.app.PurchaseRequestListRowDto;
import com.example.p2p.entity.Users;
import com.example.p2p.enums.PurchaseRequestSortBy;
import com.example.p2p.enums.PurchaseRequestStatus;
import com.example.p2p.enums.SortDirection;
import com.example.p2p.form.app.PurchaseRequestSearchForm;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PurchaseRequestMapperCustomTest {

    @Autowired
    PurchaseRequestMapperCustom purchaseRequestMapperCustom;

    @Autowired
    UsersMapper usersMapper;

    @Nested
    class SelectPurchaseRequests {

        @Test
        void selectPurchaseRequests_allCondition() {
            PurchaseRequestSearchForm form = new PurchaseRequestSearchForm();
            form.setSupplierId("a7f3c9d2-4b8e-41f1-9c6a-1d2e3f4a5b6c");
            form.setDueDateFrom(LocalDate.of(2026, 3, 20));
            form.setDueDateTo(LocalDate.of(2026, 4, 27));
            form.setStatus(PurchaseRequestStatus.COMPLETED);
            form.setKeyword("山田");

            List<PurchaseRequestListRowDto> actual = purchaseRequestMapperCustom.selectPurchaseRequests(form);

            assertThat(actual).hasSize(1);

            PurchaseRequestListRowDto first = actual.get(0);
            assertThat(first.getPrId()).isEqualTo("3a5b130e-0279-4bca-bee3-d41cddbc9192");
            assertThat(first.getDisplayNumber()).isEqualTo(2);
        }

        @Test
        void selectPurchaseRequests_noCondition() {
            PurchaseRequestSearchForm form = new PurchaseRequestSearchForm();
            form.setPage(2);

            List<PurchaseRequestListRowDto> actual = purchaseRequestMapperCustom.selectPurchaseRequests(form);

            assertThat(actual).hasSize(2);

            PurchaseRequestListRowDto first = actual.get(0);
            assertThat(first.getPrId()).isEqualTo("3c4f62bf-855b-4c35-b19d-eb06acb16896");
            assertThat(first.getDisplayNumber()).isEqualTo(3);
            assertThat(first.getRequester()).isEqualTo("鈴木 一郎");
            assertThat(first.getTotalAmountExcludingTax()).isEqualTo(39940);
            assertThat(first.getStatus()).isEqualTo(PurchaseRequestStatus.APPROVED);
        }

        @Nested
        class Filter {

            @ParameterizedTest
            @MethodSource("createFilterCaces")
            void selectPurchaseRequests_filter(Consumer<PurchaseRequestSearchForm> consumer, int expected) {
                PurchaseRequestSearchForm form = new PurchaseRequestSearchForm();
                consumer.accept(form);
                form.setSize(100);

                List<PurchaseRequestListRowDto> actual = purchaseRequestMapperCustom.selectPurchaseRequests(form);

                assertThat(actual).hasSize(expected);
            }

            static Stream<Arguments> createFilterCaces() {
                return Stream.of(
                        Arguments.of(
                                (Consumer<PurchaseRequestSearchForm>) f -> f.setDueDateFrom(LocalDate.of(2026, 5, 1)),
                                3),
                        Arguments
                            .of((Consumer<PurchaseRequestSearchForm>) f -> f.setDueDateTo(LocalDate.of(2026, 5, 1)), 1),
                        Arguments.of((Consumer<PurchaseRequestSearchForm>) f -> {
                            f.setDueDateFrom(LocalDate.of(2026, 5, 15));
                            f.setDueDateTo(LocalDate.of(2026, 8, 21));
                        }, 2),
                        Arguments.of(
                                (Consumer<PurchaseRequestSearchForm>) f -> f.setStatus(PurchaseRequestStatus.REJECTED),
                                1));
            }

            @ParameterizedTest
            @CsvSource(value = { "2026-04-25, 2026-05-01, 1", "2026-05-25, 2026-06-01, 1" })
            void selectPurchaseRequests_dateFilter_boundary(LocalDate dueDateFrom, LocalDate dueDateTo, int expected) {
                PurchaseRequestSearchForm form = new PurchaseRequestSearchForm();
                form.setSize(100);
                form.setDueDateFrom(dueDateFrom);
                form.setDueDateTo(dueDateTo);

                List<PurchaseRequestListRowDto> actual = purchaseRequestMapperCustom.selectPurchaseRequests(form);

                assertThat(actual).hasSize(expected);
            }

        }

        @Nested
        class Search {

            @ParameterizedTest
            @CsvSource(value = { "2, 1", "佐藤, 2" })
            void selectPurchaseRequests_singleWord(String keyword, int expected) {
                PurchaseRequestSearchForm form = new PurchaseRequestSearchForm();
                form.setKeyword(keyword);
                form.setSize(100);
                List<PurchaseRequestListRowDto> actual = purchaseRequestMapperCustom.selectPurchaseRequests(form);

                assertThat(actual).hasSize(expected);
            }

            @Test
            void selectItems_multiWord() {
                Users u = new Users();
                u.setUserId("6fe99043-cbd1-49c0-96d4-c156c58a8e60");
                u.setFirstName("和孝98");
                usersMapper.updateByPrimaryKeySelective(u);

                PurchaseRequestSearchForm form = new PurchaseRequestSearchForm();
                form.setKeyword("和孝 9");
                form.setSize(100);
                List<PurchaseRequestListRowDto> actual = purchaseRequestMapperCustom.selectPurchaseRequests(form);

                assertThat(actual).hasSize(1);
                assertThat(actual.get(0).getPrId()).isEqualTo("3c4f62bf-855b-4c35-b19d-eb06acb16896");
            }

            @Nested
            class Sort {

                @ParameterizedTest
                @MethodSource("createSortCases")
                void selectPurchaseRequests_sort(PurchaseRequestSortBy sortBy, SortDirection direction,
                        String includedId) {
                    PurchaseRequestSearchForm form = new PurchaseRequestSearchForm();
                    form.setSortBy(sortBy);
                    form.setSortDirection(direction);
                    List<PurchaseRequestListRowDto> actual = purchaseRequestMapperCustom.selectPurchaseRequests(form);

                    assertThat(actual).hasSize(2);
                    assertThat(actual).extracting(PurchaseRequestListRowDto::getPrId).contains(includedId);
                }

                static Stream<Arguments> createSortCases() {
                    return Stream.of(
                            Arguments.of(PurchaseRequestSortBy.NUMBER, SortDirection.DESC,
                                    "6b2c5959-233f-4b54-8a9b-98f4a1b13c40"),
                            Arguments.of(PurchaseRequestSortBy.REQUESTER, SortDirection.DESC,
                                    "3a5b130e-0279-4bca-bee3-d41cddbc9192"),
                            Arguments.of(PurchaseRequestSortBy.DUE_DATE, SortDirection.ASC,
                                    "88bfbcf6-2be6-4d31-8a46-155a7b58ab93"),
                            Arguments.of(PurchaseRequestSortBy.TOTAL, SortDirection.DESC,
                                    "6b2c5959-233f-4b54-8a9b-98f4a1b13c40"),
                            Arguments.of(PurchaseRequestSortBy.STATUS, SortDirection.ASC,
                                    "88bfbcf6-2be6-4d31-8a46-155a7b58ab93"));
                }

                @Test
                void selectPurchaseRequests_sortByDueDate_desc() {
                    PurchaseRequestSearchForm form = new PurchaseRequestSearchForm();
                    form.setPage(3);
                    form.setSortBy(PurchaseRequestSortBy.STATUS);
                    form.setSortDirection(SortDirection.DESC);
                    List<PurchaseRequestListRowDto> actual = purchaseRequestMapperCustom.selectPurchaseRequests(form);

                    assertThat(actual).hasSize(1);
                    assertThat(actual.get(0).getPrId()).isEqualTo("88bfbcf6-2be6-4d31-8a46-155a7b58ab93");
                }

            }

        }

    }

}
