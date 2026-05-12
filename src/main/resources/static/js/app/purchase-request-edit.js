$(function () {
  const details = [];
  const deletedPrDetailIds = [];
  const editBackups = new Map();
  let rowSequence = 0;

  initializeDetails();

  // =========================
  // カタログモーダル
  // =========================

  $("#openCatalogButton").on("click", function () {
    openCatalogModal();
  });

  function openCatalogModal() {
    const $modal = $("#catalogModal");
    const $content = $("#catalogModalContent");
    const url = $modal.data("catalog-modal-url");

    $content.empty();
    $content.load(url, function () {
      $modal.prop("hidden", false);
    });
  }

  $(document).on("catalog:confirmed", function (event, catalogItems) {
    catalogItems.forEach(function (item) {
      addOrMergeCatalogDetail(item);
    });

    refreshDetailArea();

    closeCatalogModal();
  });

  $(document).on("catalog:closed", function () {
    closeCatalogModal();
  });

  function closeCatalogModal() {
    $("#catalogModal").prop("hidden", true);
    $("#catalogModalContent").empty();
  }

  // =========================
  // カタログ明細追加・統合
  // =========================

  function addOrMergeCatalogDetail(item) {
    const existingDetail = details.find(function (detail) {
      return (
        detail.detailInputType === "CATALOG" && detail.itemId === item.itemId
      );
    });

    if (existingDetail) {
      existingDetail.quantity =
        Number(existingDetail.quantity) + Number(item.quantity);
      existingDetail.subtotal =
        Number(existingDetail.price) * Number(existingDetail.quantity);
      return;
    }

    details.push({
      rowId: createRowId(),
      prDetailId: "",
      detailInputType: "CATALOG",
      itemId: item.itemId,
      itemName: item.itemName,
      kind: item.kind,
      kindLabel: item.kindLabel,
      supplierId: item.supplierId,
      supplierName: item.supplierName,
      unitId: item.unitId,
      unitName: item.unitName,
      price: Number(item.price),
      quantity: Number(item.quantity),
      subtotal: calculateSubtotalValue(item.price, item.quantity),
      editing: false,
      isNew: false,
    });
  }

  // =========================
  // フリー入力行追加
  // =========================

  $("#openFreeInputButton").on("click", function () {
    details.push({
      rowId: createRowId(),
      prDetailId: "",
      detailInputType: "FREE",
      itemId: "",
      itemName: "",
      kind: "",
      kindLabel: "",
      supplierId: "",
      supplierName: "",
      unitId: "",
      unitName: "",
      price: "",
      quantity: "",
      subtotal: "",
      editing: true,
      isNew: true,
    });

    refreshDetailArea();
  });

  // =========================
  // 編集・保存・キャンセル・削除
  // =========================

  $(document).on("click", ".pr-detail-edit-button", function () {
    const index = Number($(this).data("index"));
    const detail = getDetail(index);

    editBackups.set(detail.rowId, copyDetail(detail));
    detail.editing = true;

    refreshDetailArea();
  });

  $(document).on("click", ".pr-detail-save-button", function () {
    const index = Number($(this).data("index"));
    const detail = getDetail(index);

    if (detail.detailInputType === "CATALOG") {
      saveCatalogEdit(index);
    } else if (detail.detailInputType === "FREE") {
      saveFreeEdit(index);
    }
  });

  $(document).on("click", ".pr-detail-cancel-edit-button", function () {
    const index = Number($(this).data("index"));
    const detail = getDetail(index);

    if (detail.isNew) {
      details.splice(index, 1);
      refreshDetailArea();
      return;
    }

    const backup = editBackups.get(detail.rowId);
    details[index] = backup;
    editBackups.delete(detail.rowId);

    refreshDetailArea();
  });

  $(document).on("click", ".pr-detail-delete-button", function () {
    const index = Number($(this).data("index"));

    const detail = details[index];
    editBackups.delete(detail.rowId);

    details.splice(index, 1);
    refreshDetailArea();
  });

  // =========================
  // フリー入力 手入力切替
  // =========================

  $(document).on("change", ".pr-free-supplier-select", function () {
    const $select = $(this);
    const $area = $select.closest(".pr-free-select-manual-area");
    const $input = $area.find(".pr-free-supplier-name-input");

    if ($select.val() === "manual") {
      $input.prop("hidden", false).val("");
    } else {
      $input.prop("hidden", true).val("");
    }
  });

  $(document).on("change", ".pr-free-unit-select", function () {
    const $select = $(this);
    const $area = $select.closest(".pr-free-select-manual-area");
    const $input = $area.find(".pr-free-unit-name-input");

    if ($select.val() === "manual") {
      $input.prop("hidden", false).val("");
    } else {
      $input.prop("hidden", true).val("");
    }
  });

  // =========================
  // カタログ行 編集保存
  // =========================

  function saveCatalogEdit(index) {
    const detail = getDetail(index);

    const $row = $(`.pr-detail-edit-row[data-index="${index}"]`);
    const quantityText = $row.find(".pr-edit-quantity-input").val().trim();

    detail.quantity = quantityText;
    detail.subtotal = calculateSubtotalValue(detail.price, detail.quantity);
    detail.editing = false;
    detail.isNew = false;

    editBackups.delete(detail.rowId);

    refreshDetailArea();
  }

  // =========================
  // フリー行 編集保存
  // =========================

  function saveFreeEdit(index) {
    const detail = getDetail(index);

    const $row = $(`.pr-detail-edit-row[data-index="${index}"]`);

    applyFreeEditingValues(detail, $row);

    detail.editing = false;
    detail.isNew = false;

    editBackups.delete(detail.rowId);

    refreshDetailArea();
  }

  function applyFreeEditingValues(detail, $row) {
    detail.itemName = $row.find(".pr-free-item-name-input").val().trim();
    detail.kind = $row.find(".pr-free-kind-select").val().trim();
    detail.kindLabel = $row.find(".pr-free-kind-select option:selected").text();
    detail.price = $row.find(".pr-free-price-input").val().trim();
    detail.quantity = $row.find(".pr-edit-quantity-input").val().trim();

    applySupplierFromEditingRow(detail, $row);
    applyUnitFromEditingRow(detail, $row);

    detail.subtotal = calculateSubtotalValue(detail.price, detail.quantity);
  }

  function applySupplierFromEditingRow(detail, $row) {
    const $select = $row.find(".pr-free-supplier-select");
    const selectedValue = $select.val();

    if (selectedValue === "manual") {
      detail.supplierId = "";
      detail.supplierName = $row
        .find(".pr-free-supplier-name-input")
        .val()
        .trim();
      return;
    }

    detail.supplierId = selectedValue;
    detail.supplierName = $select.find("option:selected").text();
  }

  function applyUnitFromEditingRow(detail, $row) {
    const $select = $row.find(".pr-free-unit-select");
    const selectedValue = $select.val();

    if (selectedValue === "manual") {
      detail.unitId = "";
      detail.unitName = $row.find(".pr-free-unit-name-input").val().trim();
      return;
    }

    detail.unitId = selectedValue;
    detail.unitName = $select.find("option:selected").text();
  }

  // =========================
  // 明細表示
  // =========================

  function renderDetails() {
    const $rows = $("#purchaseRequestDetailRows");
    $rows.empty();

    details.forEach(function (detail, index) {
      if (detail.editing) {
        renderEditRow($rows, detail, index);
      } else {
        renderDisplayRow($rows, detail, index);
      }
    });
  }

  function renderDisplayRow($rows, detail, index) {
    const $row = $("<tr>");

    $row.append(
      $("<td>")
        .addClass("pr-detail-col-no")
        .text(index + 1),
    );
    $row.append($("<td>").addClass("pr-detail-col-name").text(detail.itemName));
    $row.append(
      $("<td>").addClass("pr-detail-col-kind").text(getKindLabel(detail)),
    );
    $row.append(
      $("<td>").addClass("pr-detail-col-supplier").text(detail.supplierName),
    );
    $row.append($("<td>").addClass("pr-detail-col-unit").text(detail.unitName));
    $row.append(
      $("<td>")
        .addClass("pr-detail-col-price")
        .text(formatAmountText(detail.price)),
    );
    $row.append(
      $("<td>").addClass("pr-detail-col-quantity").text(detail.quantity),
    );
    $row.append(
      $("<td>")
        .addClass("pr-detail-col-subtotal")
        .text(formatAmountText(detail.subtotal)),
    );

    const $editButton = $("<button>")
      .attr("type", "button")
      .data("index", index)
      .addClass("pr-detail-edit-button")
      .text("✎");

    const $deleteButton = $("<button>")
      .attr("type", "button")
      .data("index", index)
      .addClass("pr-detail-delete-button")
      .text("🗑");

    const $actionButtons = $("<div>")
      .addClass("pr-detail-action-buttons")
      .append($editButton)
      .append($deleteButton);

    $row.append(
      $("<td>").addClass("pr-detail-col-action").append($actionButtons),
    );

    $rows.append($row);
  }

  function renderEditRow($rows, detail, index) {
    if (detail.detailInputType === "CATALOG") {
      renderCatalogEditRow($rows, detail, index);
      return;
    }

    if (detail.detailInputType === "FREE") {
      renderFreeEditRow($rows, detail, index);
    }
  }

  function renderCatalogEditRow($rows, detail, index) {
    const $row = $("<tr>")
      .addClass("pr-detail-edit-row")
      .attr("data-index", index);

    $row.append(
      $("<td>")
        .addClass("pr-detail-col-no")
        .text(index + 1),
    );
    $row.append($("<td>").addClass("pr-detail-col-name").text(detail.itemName));
    $row.append(
      $("<td>").addClass("pr-detail-col-kind").text(getKindLabel(detail)),
    );
    $row.append(
      $("<td>").addClass("pr-detail-col-supplier").text(detail.supplierName),
    );
    $row.append($("<td>").addClass("pr-detail-col-unit").text(detail.unitName));
    $row.append(
      $("<td>")
        .addClass("pr-detail-col-price")
        .text(formatAmountText(detail.price)),
    );

    const $quantityInput = $("<input>")
      .attr("type", "number")
      .attr("min", "1")
      .val(detail.quantity)
      .addClass("pr-edit-quantity-input");

    $row.append(
      $("<td>").addClass("pr-detail-col-quantity").append($quantityInput),
    );

    $row.append(
      $("<td>")
        .addClass("pr-detail-col-subtotal")
        .text(
          formatAmountText(
            calculateSubtotalValue(detail.price, detail.quantity),
          ),
        ),
    );

    $row.append(createEditActionCell(index));

    $rows.append($row);
  }

  function renderFreeEditRow($rows, detail, index) {
    const $row = $("<tr>")
      .addClass("pr-detail-edit-row")
      .attr("data-index", index);

    const $itemNameInput = $("<input>")
      .attr("type", "text")
      .val(detail.itemName)
      .addClass("pr-free-item-name-input");

    const $kindSelect = $("<select>").addClass("pr-free-kind-select");

    $kindSelect.html($("#kindOptionsTemplate").html());
    $kindSelect.val(detail.kind);

    const $priceInput = $("<input>")
      .attr("type", "number")
      .attr("min", "0")
      .val(detail.price)
      .addClass("pr-free-price-input");

    const $quantityInput = $("<input>")
      .attr("type", "number")
      .attr("min", "1")
      .val(detail.quantity)
      .addClass("pr-edit-quantity-input");

    $row.append(
      $("<td>")
        .addClass("pr-detail-col-no")
        .text(index + 1),
    );
    $row.append(
      $("<td>").addClass("pr-detail-col-name").append($itemNameInput),
    );
    $row.append($("<td>").addClass("pr-detail-col-kind").append($kindSelect));
    $row.append(
      $("<td>")
        .addClass("pr-detail-col-supplier")
        .append(createSupplierInput(detail, index)),
    );
    $row.append(
      $("<td>")
        .addClass("pr-detail-col-unit")
        .append(createUnitInput(detail, index)),
    );
    $row.append($("<td>").addClass("pr-detail-col-price").append($priceInput));
    $row.append(
      $("<td>").addClass("pr-detail-col-quantity").append($quantityInput),
    );

    $row.append(
      $("<td>")
        .addClass("pr-detail-col-subtotal")
        .text(
          formatAmountText(
            calculateSubtotalValue(detail.price, detail.quantity),
          ),
        ),
    );

    $row.append(createEditActionCell(index));

    $rows.append($row);
  }

  function createSupplierInput(detail, index) {
    const $select = $("<select>")
      .data("index", index)
      .addClass("pr-free-supplier-select");

    $select.html($("#supplierOptionsTemplate").html());

    if (detail.supplierId) {
      $select.val(detail.supplierId);
    } else if (detail.supplierName) {
      $select.val("manual");
    } else {
      $select.val("");
    }

    const $input = $("<input>")
      .attr("type", "text")
      .val(detail.supplierId ? "" : detail.supplierName)
      .addClass("pr-free-supplier-name-input");

    if (detail.supplierId || !detail.supplierName) {
      $input.prop("hidden", true);
    }

    return $("<div>")
      .addClass("pr-free-select-manual-area")
      .append($select)
      .append($input);
  }

  function createUnitInput(detail, index) {
    const $select = $("<select>")
      .data("index", index)
      .addClass("pr-free-unit-select");

    $select.html($("#unitOptionsTemplate").html());

    if (detail.unitId) {
      $select.val(detail.unitId);
    } else if (detail.unitName) {
      $select.val("manual");
    } else {
      $select.val("");
    }

    const $input = $("<input>")
      .attr("type", "text")
      .val(detail.unitId ? "" : detail.unitName)
      .addClass("pr-free-unit-name-input");

    if (detail.unitId || !detail.unitName) {
      $input.prop("hidden", true);
    }

    return $("<div>")
      .addClass("pr-free-select-manual-area")
      .append($select)
      .append($input);
  }

  function createEditActionCell(index) {
    const $saveButton = $("<button>")
      .attr("type", "button")
      .data("index", index)
      .addClass("pr-detail-save-button")
      .text("✓");

    const $cancelButton = $("<button>")
      .attr("type", "button")
      .data("index", index)
      .addClass("pr-detail-cancel-edit-button")
      .text("×");

    const $actionButtons = $("<div>")
      .addClass("pr-detail-action-buttons")
      .append($saveButton)
      .append($cancelButton);

    return $("<td>").addClass("pr-detail-col-action").append($actionButtons);
  }

  // =========================
  // hidden input 作成
  // =========================

  function renderDetailHiddenInputs() {
    const $hiddenInputs = $("#purchaseRequestDetailHiddenInputs");

    $hiddenInputs.empty();

    details.forEach(function (detail, index) {
      appendHidden(
        $hiddenInputs,
        `details[${index}].prDetailId`,
        detail.prDetailId,
      );
      appendHidden(
        $hiddenInputs,
        `details[${index}].detailInputType`,
        detail.detailInputType,
      );
      appendHidden($hiddenInputs, `details[${index}].itemId`, detail.itemId);
      appendHidden(
        $hiddenInputs,
        `details[${index}].itemName`,
        detail.itemName,
      );
      appendHidden($hiddenInputs, `details[${index}].kind`, detail.kind);
      appendHidden(
        $hiddenInputs,
        `details[${index}].supplierId`,
        detail.supplierId,
      );
      appendHidden(
        $hiddenInputs,
        `details[${index}].supplierName`,
        detail.supplierName,
      );
      appendHidden($hiddenInputs, `details[${index}].unitId`, detail.unitId);
      appendHidden(
        $hiddenInputs,
        `details[${index}].unitName`,
        detail.unitName,
      );
      appendHidden($hiddenInputs, `details[${index}].price`, detail.price);
      appendHidden(
        $hiddenInputs,
        `details[${index}].quantity`,
        detail.quantity,
      );
    });
    deletedPrDetailIds.forEach(function (prDetailId, index) {
      appendHidden($hiddenInputs, `deletedPrDetailIds[${index}]`, prDetailId);
    });
  }

  function appendHidden($container, name, value) {
    $("<input>")
      .attr("type", "hidden")
      .attr("name", name)
      .val(value)
      .appendTo($container);
  }

  // =========================
  // 金額更新
  // =========================

  function updateAmountSummary() {
    const totalAmountExcludingTax = details.reduce(function (sum, detail) {
      if (detail.subtotal === "") {
        return sum;
      }

      return sum + Number(detail.subtotal);
    }, 0);

    const taxAmount = Math.floor(totalAmountExcludingTax / 10);
    const totalAmountIncludingTax = totalAmountExcludingTax + taxAmount;

    $("#detailSubtotalAmount").text(formatNumber(totalAmountExcludingTax));
    $("#detailTaxAmount").text(formatNumber(taxAmount));
    $("#detailTotalAmount").text(formatNumber(totalAmountIncludingTax));
  }

  // =========================
  // 初期化
  // =========================

  function initializeDeletedPrDetailIds() {
    const initialDeletedPrDetailIds = window.initialDeletedPrDetailIds || [];

    initialDeletedPrDetailIds.forEach(function (prDetailId) {
      if (prDetailId) {
        deletedPrDetailIds.push(prDetailId);
      }
    });
  }

  function initializeDetails() {
    const sourceDetails = window.initialFormDetails ?? window.initialViewDetails ?? [];

    sourceDetails.forEach(function (detail) {

    });

    refreshDetailArea();
  }

  function normalizeDetail(detail) {
    
  }

  // =========================
  // 共通
  // =========================

  function refreshDetailArea() {
    renderDetails();
    renderDetailHiddenInputs();
    updateAmountSummary();
    updateSubmitButtonState();
  }

  function getDetail(index) {
    if (!Number.isInteger(index) || index < 0) {
      return null;
    }

    return details[index] || null;
  }

  function createRowId() {
    rowSequence += 1;
    return "row-" + rowSequence;
  }

  function copyDetail(detail) {
    return {
      rowId: detail.rowId,
      detailInputType: detail.detailInputType,
      itemId: detail.itemId,
      itemName: detail.itemName,
      kind: detail.kind,
      kindLabel: detail.kindLabel,
      supplierId: detail.supplierId,
      supplierName: detail.supplierName,
      unitId: detail.unitId,
      unitName: detail.unitName,
      price: detail.price,
      quantity: detail.quantity,
      subtotal: detail.subtotal,
      editing: false,
      isNew: false,
      supplierInputMode: detail.supplierInputMode,
      unitInputMode: detail.unitInputMode,
    };
  }

  function calculateSubtotalValue(price, quantity) {
    if (price === "" || quantity === "") {
      return "";
    }

    return Number(price) * Number(quantity);
  }

  function formatAmountText(value) {
    if (value === "") {
      return "";
    }

    return formatNumber(value);
  }

  function getKindLabel(detail) {
    return detail.kindLabel || findKindLabel(detail.kind) || detail.kind;
  }

  function findKindLabel(kind) {
    if (!kind) {
      return "";
    }

    return $("#kindOptionsTemplate").find(`option[value="${kind}"]`).text();
  }

  function initializeDetails() {
    const initialDetails = window.initialPurchaseRequestDetails || [];

    initialDetails.forEach(function (detail) {
      details.push({
        rowId: createRowId(),
        detailInputType: detail.detailInputType,
        itemId: detail.itemId,
        itemName: detail.itemName,
        kind: detail.kind,
        kindLabel: detail.kindLabel || findKindLabel(detail.kind),
        supplierId: detail.supplierId,
        supplierName: detail.supplierName,
        unitId: detail.unitId,
        unitName: detail.unitName,
        price: detail.price,
        quantity: detail.quantity,
        subtotal: calculateSubtotalValue(detail.price, detail.quantity),
        editing: false,
        isNew: false,
        supplierInputMode: detail.supplierId ? "select" : "manual",
        unitInputMode: detail.unitId ? "select" : "manual",
      });
    });

    refreshDetailArea();
  }

  function updateSubmitButtonState() {
    const disabled = details.length === 0 || details.some((d) => d.editing);
    $("#purchaseRequestSubmitButton").prop("disabled", disabled);
  }
});
