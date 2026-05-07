$(function () {
  const selectedItems = [];

  // =========================
  // 検索・フィルター
  // =========================

  $(document).on("click", "#catalogKeywordSearchButton", function () {
    $("#catalogSearchForm input[name=page]").val(1);
    searchCatalog($("#catalogSearchForm"));
  });

  $(document).on("keydown", "#catalogKeyword", function (event) {
    if (event.key === "Enter") {
      event.preventDefault();
      $("#catalogSearchForm input[name=page]").val(1);
      searchCatalog($("#catalogSearchForm"));
    }
  });

  $(document).on("click", "#catalogFilterSubmitButton", function () {
    $("#catalogSearchForm input[name=page]").val(1);
    searchCatalog($("#catalogSearchForm"));
  });

  $(document).on("click", "#catalogFilterResetButton", function () {
    $("#catalogSearchForm select[name=kind]").val("");
    $("#catalogSearchForm select[name=supplierId]").val("");
    $("#catalogSearchForm input[name=page]").val(1);

    searchCatalog($("#catalogSearchForm"));
  });

  $(document).on("click", ".catalog-page-link", function () {
    const page = $(this).data("page");

    $("#catalogSearchForm input[name=page]").val(page);
    searchCatalog($("#catalogSearchForm"));
  });

  /**
   * @param {JQuery<HTMLElement>} $form
   */
  function searchCatalog($form) {
    $.ajax({
      url: "/catalog/search",
      type: "GET",
      data: $form.serialize(),
    })
      .done(function (html) {
        $("#catalogSearchErrorArea").empty();
        $(".catalog-result-area").replaceWith(html);
      })
      .fail(function (xhr) {
        $("#catalogSearchErrorArea").html(xhr.responseText);
      });
  }

  // =========================
  // カタログ追加
  // =========================

  $(document).on("click", ".catalog-add-button", function () {
    const $button = $(this);
    const $row = $button.closest("tr");

    const itemId = String($button.data("item-id"));
    const itemName = $button.data("item-name");
    const kind = $button.data("kind");

    const supplierId = String($button.data("supplier-id"));
    const supplierName = $button.data("supplier-name");

    const unitId = String($button.data("unit-id"));
    const unitName = $button.data("unit-name");

    const price = Number($button.data("price"));
    const quantity = Number($row.find(".catalog-quantity-input").val());

    if (!Number.isInteger(quantity) || quantity < 1) {
      return;
    }

    const existingItem = selectedItems.find(function (item) {
      return item.itemId === itemId;
    });

    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      selectedItems.push({
        detailInputType: "CATALOG",
        itemId: itemId,
        itemName: itemName,
        kind: kind,
        supplierId: supplierId,
        supplierName: supplierName,
        unitId: unitId,
        unitName: unitName,
        price: price,
        quantity: quantity,
      });
    }

    $row.find(".catalog-quantity-input").val(1);

    updateSelectedSummary();

    if (!$("#catalogSelectedArea").prop("hidden")) {
      renderSelectedRows();
    }
  });

  // =========================
  // 追加予定の開閉
  // =========================

  $(document).on("click", "#catalogToggleSelectedAreaButton", function () {
    const isOpen = !$("#catalogSelectedArea").prop("hidden");

    if (isOpen) {
      $("#catalogSelectedArea").prop("hidden", true);
      return;
    }

    if (selectedItems.length === 0) {
      return;
    }

    renderSelectedRows();
    $("#catalogSelectedArea").prop("hidden", false);
  });

  // =========================
  // 追加予定の数量変更・削除
  // =========================

  $(document).on(
    "change",
    "#catalogSelectedRows .catalog-quantity-input",
    function () {
      const itemId = String($(this).data("item-id"));
      const quantity = Number($(this).val());

      if (!Number.isInteger(quantity) || quantity < 1) {
        return;
      }

      const item = selectedItems.find(function (item) {
        return item.itemId === itemId;
      });

      if (item) {
        item.quantity = quantity;
      }

      updateSelectedSummary();
      renderSelectedRows();
    },
  );

  $(document).on("click", ".catalog-delete-selected-button", function () {
    const itemId = String($(this).data("item-id"));

    const index = selectedItems.findIndex(function (item) {
      return item.itemId === itemId;
    });

    if (index !== -1) {
      selectedItems.splice(index, 1);
    }

    updateSelectedSummary();

    if (selectedItems.length === 0) {
      $("#catalogSelectedArea").prop("hidden", true);
      return;
    }

    renderSelectedRows();
  });

  // =========================
  // 明細へ反映・キャンセル・閉じる
  // =========================

  $(document).on("click", "#catalogConfirmButton", function () {
    if (selectedItems.length === 0) {
      return;
    }

    const catalogItems = selectedItems.map(function (item) {
      return {
        detailInputType: item.detailInputType,
        itemId: item.itemId,
        itemName: item.itemName,
        kind: item.kind,
        supplierId: item.supplierId,
        supplierName: item.supplierName,
        unitId: item.unitId,
        unitName: item.unitName,
        price: item.price,
        quantity: item.quantity,
      };
    });

    $(document).trigger("catalog:confirmed", [catalogItems]);

    clearSelectedItems();
  });

  $(document).on(
    "click",
    "#catalogCancelButton, #catalogCloseButton",
    function () {
      clearSelectedItems();

      $(document).trigger("catalog:closed");
    },
  );

  function clearSelectedItems() {
    selectedItems.splice(0);

    $("#catalogSelectedArea").prop("hidden", true);
    $("#catalogSelectedRows").empty();

    updateSelectedSummary();
  }

  // =========================
  // 追加予定サマリー更新
  // =========================

  function updateSelectedSummary() {
    const totalAmount = selectedItems.reduce(function (sum, item) {
      return sum + item.quantity * item.price;
    }, 0);

    $("#catalogSelectedCount").text(formatNumber(selectedItems.length));
    $("#catalogFooterTotalAmount").text(formatNumber(totalAmount));

    $("#catalogConfirmButton").prop("disabled", selectedItems.length === 0);
  }

  // =========================
  // 追加予定一覧描画
  // =========================

  function renderSelectedRows() {
    const $selectedRows = $("#catalogSelectedRows");

    $selectedRows.empty();

    selectedItems.forEach(function (item) {
      const subtotal = item.quantity * item.price;

      const $row = $("<tr>");

      $row.append(
        $("<td>").addClass("catalog-selected-col-name").text(item.itemName),
      );

      $row.append(
        $("<td>").addClass("catalog-selected-col-kind").text(item.kind),
      );

      $row.append(
        $("<td>")
          .addClass("catalog-selected-col-supplier")
          .text(item.supplierName),
      );

      $row.append(
        $("<td>").addClass("catalog-selected-col-unit").text(item.unitName),
      );

      $row.append(
        $("<td>")
          .addClass("catalog-selected-col-price")
          .text(formatNumber(item.price)),
      );

      const $quantityInput = $("<input>")
        .attr("type", "number")
        .attr("min", "1")
        .val(item.quantity)
        .data("item-id", item.itemId)
        .addClass("catalog-quantity-input");

      $row.append(
        $("<td>")
          .addClass("catalog-selected-col-quantity")
          .append($quantityInput),
      );

      $row.append(
        $("<td>")
          .addClass("catalog-selected-col-subtotal")
          .text(formatNumber(subtotal)),
      );

      const $deleteButton = $("<button>")
        .attr("type", "button")
        .addClass("catalog-delete-selected-button")
        .data("item-id", item.itemId)
        .text("削除");

      $row.append(
        $("<td>").addClass("catalog-selected-col-action").append($deleteButton),
      );

      $selectedRows.append($row);
    });
  }
});