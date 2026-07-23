$(function () {
  const $form = $("#detailSelectionForm");
  const $selectAllCheckBox = $("#selectAllDetails");
  const $prCheckboxes = $(".js-select-pr");
  const $detailCheckboxes = $(".js-detail-checkbox");
  const $quantityInputs = $(".js-quantity");
  const $selectedTotalAmount = $("#selectedTotalAmount");
  const $nextStepButton = $("#nextStepButton");

  /**
   * 値を数値に変換する。
   * @param {number|string} value
   */
  function parseNumber(value) {
    const normalizedValue = String(value).replace(/[,\s円]/g, "");
    return Number(normalizedValue);
  }

  /**
   * 金額を円表記に整形する。
   * @param {*} amount
   */
  function formatYen(amount) {
    return new Intl.NumberFormat("ja-JP").format(amount) + " 円";
  }

  /**
   * 指定されたPRに属する明細チェックボックスを取得する。
   * @param {*} groupIndex
   */
  function getDetailCheckboxesByGroup(groupIndex) {
    return $detailCheckboxes.filter('[data-group-index="' + groupIndex + '"]');
  }

  /**
   * 明細行の小計を取得する。
   * @param {JQuery} $detailCheckbox 明細行のチェックボックス
   */
  function getRowSubtotal($detailCheckbox) {
    const $subtotal = $detailCheckbox.closest("tr").find(".js-row-subtotal");
    return parseNumber($subtotal.attr("data-subtotal"));
  }

  /**
   * 物品明細の小計を更新する。
   * @param {JQuery} $quantityInput
   */
  function updateRowSubtotal($quantityInput) {
    const quantity = parseNumber($quantityInput.val());
    const unitPrice = parseNumber($quantityInput.attr("data-unit-price"));
    const subtotal = quantity * unitPrice;
    const $row = $quantityInput.closest("tr");
    const $subtotal = $row.find(".js-row-subtotal");

    $subtotal.attr("data-subtotal", subtotal).text(formatYen(subtotal));
  }

  /**
   * 明細行の強調表示を選択状態に合わせて更新する。
   */
  function updateSelectedRowHighlight() {
    $detailCheckboxes.each(function () {
      const $checkbox = $(this);

      $checkbox
        .closest("tr")
        .toggleClass("table-active", $checkbox.prop("checked"));
    });
  }

  /**
   * PR単位チェックボックスの状態を更新する。
   */
  function updatePrCheckbox(groupIndex) {
    const $prCheckbox = $prCheckboxes.filter(
      '[data-group-index="' + groupIndex + '"]',
    );

    const $groupDetails = getDetailCheckboxesByGroup(groupIndex);

    const totalCount = $groupDetails.length;
    const checkedCount = $groupDetails.filter(":checked").length;

    $prCheckbox
      .prop("checked", checkedCount === totalCount)
      .prop("indeterminate", checkedCount > 0 && checkedCount < totalCount);
  }

  /**
   * 全明細選択チェックボックスの状態を更新する。
   */
  function updateSelectAllCheckbox() {
    const totalCount = $detailCheckboxes.length;
    const checkedCount = $detailCheckboxes.filter(":checked").length;

    $selectAllCheckBox
      .prop("checked", checkedCount === totalCount)
      .prop("indeterminate", checkedCount > 0 && checkedCount < totalCount);
  }

  /**
   * 選択合計と次へボタンの状態を更新する。
   */
  function updateSelectionSummary() {
    const $checkedDetailCheckboxes = $detailCheckboxes.filter(":checked");

    let selectedTotal = 0;
    let hasInvalidQuantity = false;

    $checkedDetailCheckboxes.each(function () {
      const $row = $(this).closest("tr");
      const $quantityInput = $row.find(".js-quantity");

      // 物品明細に数量入力エラーがあるか確認する。
      // サービス明細には数量入力欄がないため判定対象外。
      if (
        $quantityInput.length > 0 &&
        isInvalidQuantityInput($quantityInput)
      ) {
        hasInvalidQuantity = true;
        return;
      }

      selectedTotal += getRowSubtotal($(this));
    });

    $selectedTotalAmount.text(formatYen(selectedTotal));

    $nextStepButton.prop(
      "disabled",
      $checkedDetailCheckboxes.length === 0 || hasInvalidQuantity,
    );
  }

  /**
   * 画面全体の選択状態を更新する。
   */
  function refreshSelectionState() {
    $prCheckboxes.each(function () {
      const groupIndex = $(this).attr("data-group-index");

      updatePrCheckbox(groupIndex);
    });

    updateSelectAllCheckbox();
    updateSelectionSummary();
    updateSelectedRowHighlight();
  }

  /**
   * 選択された物品明細から、
   * 不正な数量入力欄全てを取得する。
   *
   * @returns {JQuery} 不正な数量入力欄。
   *                   存在しない場合は空のjQueryオブジェクト
   */
  function findInvalidQuantityInputs() {
    let $invalidInputs = $();

    $detailCheckboxes.filter(":checked").each(function () {
      const $quantityInput = $(this).closest("tr").find(".js-quantity");

      // サービス明細には数量入力欄が存在しない。
      if ($quantityInput.length === 0) {
        return;
      }

      if (isInvalidQuantityInput($quantityInput)) {
        $quantityInput.addClass("is-invalid");
        $invalidInputs = $invalidInputs.add($quantityInput);
      }
    });
    return $invalidInputs;
  }

  /**
   * 全明細選択チェックボックス変更時。
   */
  $selectAllCheckBox.on("change", function () {
    const checked = $(this).prop("checked");

    $(this).prop("indeterminate", false);

    $detailCheckboxes.prop("checked", checked);

    refreshSelectionState();
  });

  /**
   * PR単位チェックボックス変更時。
   */
  $prCheckboxes.on("change", function () {
    const $prCheckbox = $(this);

    const groupIndex = $prCheckbox.attr("data-group-index");

    const checked = $prCheckbox.prop("checked");

    getDetailCheckboxesByGroup(groupIndex).prop("checked", checked);

    refreshSelectionState();
  });

  /**
   * 明細単位チェックボックス変更時。
   */
  $detailCheckboxes.on("change", function () {
    const groupIndex = $(this).attr("data-group-index");

    updatePrCheckbox(groupIndex);
    updateSelectAllCheckbox();
    updateSelectionSummary();
    updateSelectedRowHighlight();
  });

  /**
   * 物品数量欄の値が確定したとき、
   * 入力値を検証し、正常な場合だけ小計を更新する。
   */
  $quantityInputs.on("change", function () {
    const $quantityInput = $(this);

    const isInvalid = isInvalidQuantityInput($quantityInput);

    $quantityInput.toggleClass("is-invalid", isInvalid);

    // 不正な値の場合は、小計を変更しない。
    if (!isInvalid) {
      updateRowSubtotal($quantityInput);
    }

    updateSelectionSummary();
  });

  /**
   * 数量入力欄の値が不正か判定する。
   * @param {JQuery} $quantityInput 数量入力欄
   * @returns {boolean} 不正な場合はtrue
   */
  function isInvalidQuantityInput($quantityInput) {
    const rawValue = $quantityInput.val();
    const quantity = Number(rawValue);

    const min = parseNumber($quantityInput.attr("min"));

    const max = parseNumber($quantityInput.attr("max"));

    return (
      rawValue === "" ||
      !Number.isFinite(quantity) ||
      !Number.isInteger(quantity) ||
      quantity < min ||
      quantity > max
    );
  }

  /**
   * フォーム送信時。
   */
  $form.on("submit", function (event) {
    const selectedCount = $detailCheckboxes.filter(":checked").length;

    if (selectedCount === 0) {
      event.preventDefault();
      window.alert("発注対象の明細を1件以上選択してください。");
      return;
    }

    const $invalidQuantityInputs = findInvalidQuantityInputs();
    if ($invalidQuantityInputs.length > 0) {
      event.preventDefault();
      window.alert("発注数量を正しく入力してください。");

      $invalidQuantityInputs.first().trigger("focus");
      return;
    }

    $nextStepButton.prop("disabled", true).text("処理中...");
  });

  /**
   * 初期表示処理。
   *
   * 初回表示時のForm初期値および
   * BindingResultから復元された入力値を小計へ反映する。
   */
  $quantityInputs.each(function () {
    const $quantityInput = $(this);
    const isInvalid = isInvalidQuantityInput($quantityInput);

    // 入力値から検証状態を決定し、CSSクラスは表示結果として設定する。
    $quantityInput.toggleClass("is-invalid", isInvalid);

    if (isInvalid) {
      return;
    }
    updateRowSubtotal($quantityInput);
  });

  refreshSelectionState();
});
