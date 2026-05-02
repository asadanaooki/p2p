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

    PurchaseRequestDetailForm form = new PurchaseRequestDetailForm();

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
    void validPurchaseRequestDetailForm_ng(Consumer<PurchaseRequestDetailForm> consumer) {
        PurchaseRequestDetailForm form = baseForm();
        consumer.accept(form);

        BindingResult bindingResult = new BindException(form, "PurchaseRequestDetailForm");
        validator.validate(form, bindingResult);

        assertThat(bindingResult.hasErrors()).isTrue();
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
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemId(null)),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemId("a".repeat(36))),
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
                // quantity
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setQuantity(null)),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setQuantity(0)),

                // Catalog
                // itemId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.CATALOG);
                    c.setItemId(null);
                }),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.CATALOG);
                    c.setItemId("a".repeat(35));
                }),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> {
                    c.setDetailInputType(DetailInputType.CATALOG);
                    c.setItemId("a".repeat(37));
                }),

                // Free
                // kind
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setKind(null)),
                // itemName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemName(null)),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemName("")),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setItemName("あ".repeat(101))),
                // supplierId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierId("a".repeat(35))),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierId("a".repeat(37))),
                // supplierName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierName(null)),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierName(" ")),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setSupplierName("あ".repeat(101))),
                // unitId
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitId("a".repeat(35))),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitId("a".repeat(37))),
                // unitName
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitName(null)),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitName("　")),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setUnitName("あ".repeat(101))),
                // price
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setPrice(null)),
                Arguments.of((Consumer<PurchaseRequestDetailForm>) c -> c.setPrice(-1))

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
