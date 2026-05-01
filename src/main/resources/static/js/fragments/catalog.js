$(function () {
  const selectedItems = [];

  // =========================
  // 検索・フィルター
  // =========================

  $("#catalogKeywordSearchButton").on("click", function () {
    $("#catalogSearchForm input[name=page]").val(1);
    searchCatalog($("#catalogSearchForm"));
  });

  $("#catalogKeyword").on("keydown", function (event) {
    if (event.key === "Enter") {
      event.preventDefault();
      $("#catalogSearchForm input[name=page]").val(1);
      searchCatalog($("#catalogSearchForm"));
    }
  });

  $("#catalogFilterSubmitButton").on("click", function () {
    $("#catalogSearchForm input[name=page]").val(1);
    searchCatalog($("#catalogSearchForm"));
  });

  $("#catalogFilterResetButton").on("click", function () {
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
    const name = $button.data("name");
    const kind = $button.data("kind");
    const supplier = $button.data("supplier");
    const unit = $button.data("unit");
    const price = Number($button.data("price"));

    const quantity = Number($row.find(".catalog-quantity-input").val());

    if (!Number.isInteger(quantity) || quantity < 1) {
      return;
    }

    const existingItem = selectedItems.find((item) => item.itemId === itemId);

    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      selectedItems.push({
        itemId,
        name,
        kind,
        supplier,
        unit,
        price,
        quantity,
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

  $("#catalogToggleSelectedAreaButton").on("click", function () {
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

  $(document).on("change", "#catalogSelectedRows .catalog-quantity-input", function () {
    const itemId = String($(this).data("item-id"));
    const quantity = Number($(this).val());

    if (!Number.isInteger(quantity) || quantity < 1) {
      return;
    }

    const item = selectedItems.find((item) => item.itemId === itemId);

    if (item) {
      item.quantity = quantity;
    }

    updateSelectedSummary();
    renderSelectedRows();
  });

  $(document).on("click", ".catalog-delete-selected-button", function () {
    const itemId = String($(this).data("item-id"));
    const index = selectedItems.findIndex((item) => item.itemId === itemId);

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

  // TODO:

  // =========================
  // 追加予定サマリー更新
  // =========================

  function updateSelectedSummary() {
    const totalAmount = selectedItems.reduce(function (sum, item) {
      return sum + item.quantity * item.price;
    }, 0);

    $("#catalogSelectedCount").text(formatNumber(selectedItems.length));
    $("#catalogFooterTotalAmount").text(formatNumber(totalAmount));
  }

  function formatNumber(value) {
    return Number(value).toLocaleString("ja-JP");
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

      $row.append($("<td>").addClass("catalog-selected-col-name").text(item.name));
      $row.append($("<td>").addClass("catalog-selected-col-kind").text(item.kind));
      $row.append(
        $("<td>").addClass("catalog-selected-col-supplier").text(item.supplier),
      );
      $row.append($("<td>").addClass("catalog-selected-col-unit").text(item.unit));
      $row.append(
        $("<td>").addClass("catalog-selected-col-price").text(formatNumber(item.price)),
      );

      const $quantityInput = $("<input>")
        .attr("type", "number")
        .attr("min", "1")
        .val(item.quantity)
        .data("item-id", item.itemId)
        .addClass("catalog-quantity-input");

      $row.append(
        $("<td>").addClass("catalog-selected-col-quantity").append($quantityInput),
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