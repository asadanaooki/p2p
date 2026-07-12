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
    const $detailCheckbox = $row.find(".js-detail-checkbox");

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

    $checkedDetailCheckboxes.each(function () {
      selectedTotal += getRowSubtotal($(this));
    });

    $selectedTotalAmount.text(formatYen(selectedTotal));

    $nextStepButton.prop("disabled", $checkedDetailCheckboxes.length === 0);
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

  function findInvalidQuantityInput() {
    let $invalidInput = $();

    $detailCheckboxes.filter(":checked").each(function () {
      const $quantityInput = $(this).closest("tr").find(".js-quantity");

      // サービス明細には数量入力欄が存在しない。
      if ($quantityInput.length === 0) {
        return;
      }

      const rawValue = $quantityInput.val();
      const quantity = Number(rawValue);

      const min = parseNumber($quantityInput.attr("min"));
      const max = parseNumber($quantityInput.attr("max"));

      const isInvalid =
        rawValue === "" ||
        !Number.isFinite(quantity) ||
        !Number.isInteger(quantity) ||
        quantity < min ||
        quantity > max;

      if (isInvalid) {
        $invalidInput = $quantityInput;
        return false;
      }
    });
    return $invalidInput;
  }
});
