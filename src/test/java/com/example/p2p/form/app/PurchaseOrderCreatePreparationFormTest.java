package com.example.p2p.form.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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

import com.example.p2p.enums.PurchaseOrderType;
import com.example.p2p.form.app.PurchaseOrderCreatePreparationForm.PurchaseRequestDetailSelectionRowForm;

@SpringBootTest
class PurchaseOrderCreatePreparationFormTest {

    @Autowired
    Validator validator;

    @ParameterizedTest(name = "{0}")
    @MethodSource("okCases")
    void validate_ok(String caseName, Consumer<PurchaseOrderCreatePreparationForm> modifier) {
        PurchaseOrderCreatePreparationForm form = standardBaseForm();
        modifier.accept(form);

        BindingResult bindingResult = validate(form);

        assertThat(bindingResult.hasErrors()).as(caseName).isFalse();
        assertThat(form.isAnyDetailSelected()).isTrue();
        assertThat(form.isSelectedQuantityValid()).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ngCases")
    void validate_ng(String caseName, Consumer<PurchaseOrderCreatePreparationForm> modifier,
            String expectedField) {
        PurchaseOrderCreatePreparationForm form = standardBaseForm();
        modifier.accept(form);

        BindingResult bindingResult = validate(form);

        assertThat(bindingResult.getFieldErrors())
            .as(caseName)
            .extracting(error -> error.getField())
            .containsExactly(expectedField);
    }

    static Stream<Arguments> okCases() {
        return Stream.of(
                Arguments.of("STANDARD：選択済み明細に数量が設定されている",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form
                            .setOrderType(PurchaseOrderType.STANDARD)),
                Arguments.of("SERVICE：選択済み明細の数量がnullである",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> {
                            form.setOrderType(PurchaseOrderType.SERVICE);
                            form.setDetails(List.of(
                                    detail("detail-1", null, true),
                                    detail("detail-2", null, true),
                                    detail("detail-3", 1, false)));
                        }));
    }

    static Stream<Arguments> ngCases() {
        return Stream.of(
                Arguments.of("orderTypeがnull",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form.setOrderType(null),
                        "orderType"),
                Arguments.of("detailsが空",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form.setDetails(List.of()),
                        "details"),
                Arguments.of("detailsがnull",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form.setDetails(null),
                        "details"),
                Arguments.of("選択済み明細が0件",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form.getDetails()
                            .forEach(detail -> detail.setSelected(false)),
                        "anyDetailSelected"),
                Arguments.of("STANDARD：2件目の選択済み明細の数量がnull",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form.getDetails()
                            .get(1)
                            .setSelectedQuantity(null),
                        "selectedQuantityValid"),
                Arguments.of("SERVICE：選択済み明細の1件だけに数量が設定されている",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> {
                            form.setOrderType(PurchaseOrderType.SERVICE);
                            form.setDetails(List.of(
                                    detail("detail-1", null, true),
                                    detail("detail-2", 1, true),
                                    detail("detail-3", 1, false)));
                        },
                        "selectedQuantityValid"),
                Arguments.of("detailsのprDetailIdが空欄",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form.getDetails()
                            .get(0)
                            .setPrDetailId(""),
                        "details[0].prDetailId"),
                Arguments.of("detailsのselectedQuantityが0",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form.getDetails()
                            .get(0)
                            .setSelectedQuantity(0),
                        "selectedQuantityValid"),
                Arguments.of("detailsのselectedがnull",
                        (Consumer<PurchaseOrderCreatePreparationForm>) form -> form.getDetails()
                            .get(0)
                            .setSelected(null),
                        "details[0].selected"));
    }

    private BindingResult validate(PurchaseOrderCreatePreparationForm form) {
        BindingResult bindingResult = new BindException(form, "purchaseOrderCreatePreparationForm");
        validator.validate(form, bindingResult);
        return bindingResult;
    }

    private static PurchaseOrderCreatePreparationForm standardBaseForm() {
        PurchaseOrderCreatePreparationForm form = new PurchaseOrderCreatePreparationForm();
        form.setSupplierId("supplier-1");
        form.setSupplierName("テスト仕入先");
        form.setOrderType(PurchaseOrderType.STANDARD);
        PurchaseRequestDetailSelectionRowForm detail1 = detail("detail-1", 1, true);
        detail1.setOrderMaxQuantity(10);
        PurchaseRequestDetailSelectionRowForm detail2 = detail("detail-2", 2, true);
        detail2.setOrderMaxQuantity(20);
        PurchaseRequestDetailSelectionRowForm detail3 = detail("detail-3", null, false);
        detail3.setOrderMaxQuantity(30);
        form.setDetails(List.of(detail1, detail2, detail3));
        return form;
    }

    private static PurchaseRequestDetailSelectionRowForm detail(String prDetailId, Integer selectedQuantity,
            Boolean selected) {
        PurchaseRequestDetailSelectionRowForm detail = new PurchaseRequestDetailSelectionRowForm();
        detail.setPrDetailId(prDetailId);
        detail.setSelectedQuantity(selectedQuantity);
        detail.setSelected(selected);
        return detail;
    }
}
