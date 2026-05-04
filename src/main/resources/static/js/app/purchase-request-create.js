$(function () {
  const details = [];

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
    $content.load(url);
    $modal.prop("hidden", false);
  }

  $(document).on("catalog:confirmed", function (event, catalogItems) {
    catalogItems.forEach(function (item) {
      addOrMergeCatalogDetail(item);
    });

    // TODO:

    closeCatalogModal();
  });

  function closeCatalogModal() {
    $("#catalogModal").prop("hidden", true);
    $("#catalogModalContent").empty();
  }

  // =========================
  // 明細追加・統合
  // =========================

  function addOrMergeCatalogDetail(item) {
    const existingDetail = details.find(function (detail) {
      return (
        detail.detailInputType === "CATALOG" && detail.itemId === item.itemId
      );
    });

    if (existingDetail) {
      existingDetail.quantity += item.quantity;
      existingDetail.subtotal = existingDetail.price * existingDetail.quantity;
      return;
    }

    details.push({
      detailInputType: "CATALOG",
      itemId: item.itemId,
      itemName: item.itemName,
      kind: item.kind,
      supplierId: item.supplierId,
      supplierName: item.supplierName,
      unitId: item.unitId,
      unitName: item.unitName,
      price: item.price,
      quantity: item.quantity,
      subtotal: item.subtotal,
    });
  }

  // =========================
  // 明細表示
  // =========================

  function renderDetails() {
    const $rows = $("#purchaseRequestDetailRows");
    $rows.empty();

    details.forEach(function (detail, index) {
      const $row = $("<tr>");

      $row.append("<td>").addClass("pr-detail-col-name").text(detail.itemName);
      $row.append("<td>").addClass("pr-detail-col-kind").text(detail.kind);
      $row
        .append("<td>")
        .addClass("pr-detail-col-supplier")
        .text(detail.supplierName);
      $row.append("<td>").addClass("pr-detail-col-unit").text(detail.unitName);
      $row.append(
        $("<td>")
          .addClass("pr-detail-col-price")
          .text(formatNumber(detail.price)),
      );

      const $quantityInput = $("<input>")
        .attr("type", "number")
        .attr("min", "1")
        .val(detail.quantity)
        .data("index", index)
        .addClass("pr-detail-quantity-input");

      $row.append(
        $("<td>").addClass("pr-detail-col-quantity").append($quantityInput),
      );

      $row.append(
        $("<td>")
          .addClass("pr-detail-col-subtotal")
          .text(formatNumber(detail.subtotal)),
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

      $row.append(
        $("<td>")
          .addClass("pr-detail-col-action")
          .append($editButton)
          .append($deleteButton),
      );

      $rows.append($row);
    });
  }
});
