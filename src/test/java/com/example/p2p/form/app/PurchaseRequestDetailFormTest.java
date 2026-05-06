package com.example.p2p.form.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.example.p2p.enums.DetailInputType;
import com.example.p2p.enums.ItemKind;

@SpringBootTest
class PurchaseRequestDetailFormTest {

    @Autowired
    Validator validator;

    @ParameterizedTest
    @MethodSource("createOkCases")
    void validPurchaseRequestDetailForm_ok(Consumer<PurchaseRequestDetailForm> consumer) {
        PurchaseRequestDetailForm form = baseForm();
        consumer.accept(form);

        BindingResult bindingResult = new BindException(form, "PurchaseRequestDetailForm");
        validator.validate(form, bindingResult);

        assertThat(bindingResult.hasErrors()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("createNgCases")
    void validPurchaseRequestDetailForm_ng(Consumer<PurchaseRequestDetailForm> consumer, String expMessage) {
        PurchaseRequestDetailForm form = baseForm();
        consumer.accept(form);

        BindingResult bindingResult = new BindException(form, "purchaseRequestDetailForm");
        validator.validate(form, bindingResult);

        assertThat(bindingResult.hasErrors()).isTrue();
        
        assertThat(bindingResult.getAllErrors()).hasSize(1);
        assertThat(bindingResult.getAllErrors().get(0).getDefaultMessage()).isEqualTo(expMessage);
    }

    static Stream<Arguments> createOkCases() {
        return Stream.of(
                // quantity
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.CATALOG);
                    c.setQuantity(1);
                }), 
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.FREE);
                    c.setQuantity(1);
                }),
                // Catalog
                // itemId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.CATALOG);
                    c.setItemId("a".repeat(36));
                }),

                // Free
                // itemId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemId(null)),
                // itemName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemName("あ".repeat(100))),
                // supplierId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierId(null)),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierId("a".repeat(36))),
                // supplierName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierName("あ".repeat(100))),
                // unitId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitId(null)),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitId("a".repeat(36))),
                // unitName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitName("あ".repeat(50))),
                // price
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setPrice(0)));
    }

    static Stream<Arguments> createNgCases() {
        return Stream.of(
                // detailInputType
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setDetailInputType(null), "明細情報が不正です。"),
                // quantity
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setQuantity(null), "数量は1以上で入力してください。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setQuantity(0), "数量は1以上で入力してください。"),

                // Catalog
                // itemId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.CATALOG);
                    c.setItemId(null);
                }, "カタログ明細のアイテム情報が不正です。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.CATALOG);
                    c.setItemId("a".repeat(35));
                }, "カタログ明細のアイテム情報が不正です。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.CATALOG);
                    c.setItemId("a".repeat(37));
                }, "カタログ明細のアイテム情報が不正です。"),

                // Free
                // kind
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setKind(null), "種別を選択してください。"),
                // itemName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemName(null), "品名は100文字以内で入力してください。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemName("あ".repeat(101)), "品名は100文字以内で入力してください。"),
                // supplierId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierId("a".repeat(35)), "仕入先情報が不正です。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierId("a".repeat(37)), "仕入先情報が不正です。"),
                // supplierName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierName(null), "仕入先名は100文字以内で入力してください。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierName("あ".repeat(101)), "仕入先名は100文字以内で入力してください。"),
                // unitId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitId("a".repeat(35)), "単位情報が不正です。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitId("a".repeat(37)), "単位情報が不正です。"),
                // unitName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitName(null), "単位名は50文字以内で入力してください。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitName("あ".repeat(51)), "単位名は50文字以内で入力してください。"),
                // price
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setPrice(null), "単価は0円以上で入力してください。"),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setPrice(-1), "単価は0円以上で入力してください。")

        );
    }

    PurchaseRequestDetailForm baseForm() {
        PurchaseRequestDetailForm form = new PurchaseRequestDetailForm();
        form.setDetailInputType(DetailInputType.FREE);
        form.setItemId(UUID.randomUUID().toString());
        form.setKind(ItemKind.GOODS);
        form.setItemName("testItem");
        form.setSupplierId(UUID.randomUUID().toString());
        form.setSupplierName("testSupplier");
        form.setUnitId(UUID.randomUUID().toString());
        form.setUnitName("testSupplier");
        form.setPrice(1500);
        form.setQuantity(3);

        return form;
    }

}
