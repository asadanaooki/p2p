$(function () {
  const selectedItems = [];

  // =========================
  // 検索・フィルター
  // =========================

  $("#keywordSearchButton").on("click", function () {
    $("#catalogKeywordForm input[name=page]").val(1);
    searchCatalog($("#catalogKeywordForm"));
  });

  $("#keyword").on("keydown", function (event) {
    if (event.key === "Enter") {
      event.preventDefault();
      $("#catalogKeywordForm input[name=page]").val(1);
      searchCatalog($("#catalogKeywordForm"));
    }
  });

  $("#filterSubmitButton").on("click", function () {
    $("#catalogFilterForm input[name=page]").val(1);
    searchCatalog($("#catalogFilterForm"));
  });

  $("#filterResetButton").on("click", function () {
    $("#catalogFilterForm select[name=kind]").val("");
    $("#catalogFilterForm select[name=supplierId]").val("");
    $("#catalogFilterForm input[name=page]").val(1);

    searchCatalog($("#catalogFilterForm"));
  });

  $(document).on("click", ".page-link", function () {
    const page = $(this).data("page");

    $("#catalogKeywordForm input[name=page]").val(page);
    $("#catalogFilterForm input[name=page]").val(page);

    searchCatalog($("#catalogKeywordForm"));
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

  $(document).on("click", ".add-button", function () {
    const $button = $(this);
    const $row = $button.closest("tr");

    const itemId = String($button.data("item-id"));
    const name = $button.data("name");
    const kind = $button.data("kind");
    const supplier = $button.data("supplier");
    const unit = $button.data("unit");
    const price = Number($button.data("price"));

    const quantity = Number($row.find(".quantity-input").val());

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

    $row.find(".quantity-input").val(1);

    updateSelectedSummary();

    if (!$("#selectedArea").prop("hidden")) {
      renderSelectedRows();
    }
  });

  // =========================
  // 追加予定の開閉
  // =========================

  $("#toggleSelectedAreaButton").on("click", function () {
    const isOpen = !$("#selectedArea").prop("hidden");

    if (isOpen) {
      $("#selectedArea").prop("hidden", true);
      return;
    }

    renderSelectedRows();
    $("#selectedArea").prop("hidden", false);
  });

  // =========================
  // 追加予定の数量変更・削除
  // =========================

  $(document).on("change", "#selectedRows .quantity-input", function () {
    const itemId = $(this).data("item-id");
    const quantity = NUmber($(this).val());

    const item = selectedItems.find((item) => item.itemId === itemId);

    if (item) {
      item.quantity = quantity;
    }

    updateSelectedSummary();
    renderSelectedRows();
  });

  $(document).on("click", ".delete-selected-button", function () {
    const itemId = $(this).data("item-id");
    const index = selectedItems.findIndex((item) => item.itemId === itemId);

    if (index != -1) {
      selectedItems.splice(index, 1);
    }

    updateSelectedSummary();
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

    $("#selectedCount").text(formatNumber(selectedItems.length));
    $("#footerTotalAmount").text(formatNumber(totalAmount));
  }

  function formatNumber(value) {
    return Number(value).toLocaleString("ja-JP");
  }

  // =========================
  // 追加予定一覧描画
  // =========================

  function renderSelectedRows() {
    const $selectedRows = $("#selectedRows");

    $selectedRows.empty();

    selectedItems.forEach(function (item) {
      const subtotal = item.quantity * item.price;

      const $row = $("<tr>");

      $row.append($("<td>").addClass("selected-col-name").text(item.name));
      $row.append($("<td>").addClass("selected-col-kind").text(item.kind));
      $row.append(
        $("<td>").addClass("selected-col-supplier").text(item.supplier),
      );
      $row.append($("<td>").addClass("selected-col-unit").text(item.unit));
      $row.append(
        $("<td>").addClass("selected-col-price").text(formatNumber(item.price)),
      );

      const $quantityInput = $("<input>")
        .attr("type", "number")
        .attr("min", "1")
        .val(item.quantity)
        .data("item-id", item.itemId)
        .addClass("quantity-input");

      $row.append(
        $("<td>").addClass("selected-col-quantity").append($quantityInput),
      );

      $row.append(
        $("<td>")
          .addClass("selected-col-subtotal")
          .text(formatNumber(subtotal)),
      );

      const $deleteButton = $("<button>")
        .attr("type", "button")
        .addClass("delete-selected-button")
        .data("item-id", item.itemId)
        .text("削除");

      $row.append(
        $("<td>").addClass("selected-col-action").append($deleteButton),
      );

      $selectedRows.append($row);
    });
  }
});
