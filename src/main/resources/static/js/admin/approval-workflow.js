$(function () {
  // ==================================================
  // 状態管理
  // ==================================================
  let $currentStep = null;
  let $editingStep = null;
  let $editingApproverCard = null;


  // ==================================================
  // ステップ関連イベント
  // ==================================================

  // ステップ追加モーダルを開く
  $(".js-open-step-modal").on("click", function () {
    $editingStep = null;

    $("#stepModalTitle").text("ステップを追加");
    $("#stepName").val("");

    hideStepNameError();
    openModal("#stepModal");
  });

  // ステップ編集モーダルを開く
  $(document).on("click", ".js-edit-step", function () {
    $editingStep = $(this).closest(".workflow-step-block");

    const stepName = $editingStep.find(".step-name").text().trim();

    $("#stepModalTitle").text("ステップを編集");
    $("#stepName").val(stepName);

    hideStepNameError();
    openModal("#stepModal");
  });

  // ステップ追加・編集を確定
  $(".js-save-step").on("click", function () {
    const stepName = $("#stepName").val().trim();

    if (stepName === "") {
      showStepNameError();
      return;
    }

    if ($editingStep) {
      updateStep($editingStep, stepName);
    } else {
      addStep(stepName);
      refreshStepNumbers();
    }

    closeModal();
  });

  // ステップ削除
  $(document).on("click", ".js-delete-step", function () {
    const $step = $(this).closest(".workflow-step-block");
    const stepId = $step.data("step-id");

    if (stepId) {
      // DBに存在するステップは、保存時に削除対象として送るため非表示にする
      $step.attr("data-deleted", "true");
      $step.addClass("is-deleted");
    } else {
      // 新規追加した未保存ステップはDOMから削除する
      $step.remove();
    }

    refreshStepNumbers();
  });


  // ==================================================
  // 承認者関連イベント
  // ==================================================

  // 承認者追加モーダルを開く
  $(document).on("click", ".js-approver-modal", function () {
    $currentStep = $(this).closest(".workflow-step-block");
    $editingApproverCard = null;

    $("#approverModalTitle").text("承認者を追加");
    $("#approverUserId").val("");
    $("#thresholdFrom").val("");
    $("#thresholdTo").val("");

    hideApproverErrors();
    openModal("#approverModal");
  });

  // 承認者編集モーダルを開く
  $(document).on("click", ".js-edit-approver", function () {
    $editingApproverCard = $(this).closest(".approver-card");

    const userId = $editingApproverCard.attr("data-user-id");
    const thresholdFrom = $editingApproverCard.attr("data-threshold-from");
    const thresholdTo = $editingApproverCard.attr("data-threshold-to");

    $("#approverModalTitle").text("承認者を編集");
    $("#approverUserId").val(userId);
    $("#thresholdFrom").val(thresholdFrom);
    $("#thresholdTo").val(thresholdTo || "");

    hideApproverErrors();
    openModal("#approverModal");
  });

  // 承認者追加・編集を確定
  $(".js-save-approver").on("click", function () {
    hideApproverErrors();

    const approver = getApproverFormValue();

    let hasError = false;

    if (approver.userId === "") {
      $("#approverUserId").addClass("is-error");
      $("#approverUserError")
        .text("承認者を選択してください。")
        .removeClass("is-hidden");

      hasError = true;
    }

    if (approver.thresholdFrom === "") {
      $("#thresholdFrom").addClass("is-error");
      $("#thresholdFromError")
        .text("承認閾値の最小額を入力してください。")
        .removeClass("is-hidden");

      hasError = true;
    }

    if (hasError) {
      return;
    }

    if ($editingApproverCard) {
      updateApproverCard($editingApproverCard, approver);
      closeModal();
    } else {
      addApprover($currentStep, approver);
    }
  });

  // 承認者削除
  $(document).on("click", ".js-delete-approver", function () {
    const $approverList = $(this).closest(".approver-list");

    $(this).closest(".approver-card").remove();

    if ($approverList.find(".approver-card").length === 0) {
      $approverList.removeClass("has-approvers");
    }
  });


  // ==================================================
  // 共通イベント
  // ==================================================

  // モーダルを閉じる
  $(".js-close-modal").on("click", function () {
    closeModal();
  });


  // ==================================================
  // ステップ関連関数
  // ==================================================

  function addStep(stepName) {
    const $step = $($("#stepTemplate").html());

    $step.find(".step-name").text(stepName);

    $(".workflow-add-step").before($step);
  }

  function updateStep($step, stepName) {
    $step.find(".step-name").text(stepName);
  }

  function refreshStepNumbers() {
    $(".workflow-step-block")
      .not(".is-deleted")
      .each(function (index) {
        $(this)
          .find(".step-number")
          .text(index + 1);
      });
  }


  // ==================================================
  // 承認者関連関数
  // ==================================================

  function getApproverFormValue() {
    return {
      userId: $("#approverUserId").val(),
      userName: $("#approverUserId option:selected").text(),
      email: $("#approverUserId option:selected").data("email"),
      thresholdFrom: $("#thresholdFrom").val(),
      thresholdTo: $("#thresholdTo").val(),
    };
  }

  function addApprover($step, approver) {
    const $approverList = $step.find(".approver-list");

    const $approverCard = $(`
      <div class="approver-card">
        <div class="approver-card-actions">
          <button type="button" class="approver-card-button js-edit-approver">✎</button>
          <button type="button" class="approver-card-button danger js-delete-approver">×</button>
        </div>

        <div class="approver-name"></div>
        <div class="approver-email"></div>
        <div class="approver-threshold"></div>
      </div>
    `);

    updateApproverCard($approverCard, approver);

    $approverList.append($approverCard);
    $approverList.addClass("has-approvers");

    closeModal();
  }

  function updateApproverCard($approverCard, approver) {
    $approverCard.attr("data-user-id", approver.userId);
    $approverCard.attr("data-email", approver.email);
    $approverCard.attr("data-threshold-from", approver.thresholdFrom);
    $approverCard.attr("data-threshold-to", approver.thresholdTo);

    $approverCard.find(".approver-name").text(approver.userName);
    $approverCard.find(".approver-email").text(approver.email);
    $approverCard.find(".approver-threshold").text(createThresholdText(approver));
  }

  function createThresholdText(approver) {
    let thresholdText =
      "承認閾値：" + Number(approver.thresholdFrom).toLocaleString() + "円 ～";

    if (approver.thresholdTo !== "") {
      thresholdText +=
        " " + Number(approver.thresholdTo).toLocaleString() + "円";
    }

    return thresholdText;
  }


  // ==================================================
  // モーダル・エラー共通関数
  // ==================================================

  function openModal(selector) {
    $(selector).removeClass("is-hidden");
  }

  function closeModal() {
    $(".modal-overlay").addClass("is-hidden");

    $editingStep = null;
    $editingApproverCard = null;
  }

  function showStepNameError() {
    $("#stepName").addClass("is-error");
    $("#stepNameError").removeClass("is-hidden");
  }

  function hideStepNameError() {
    $("#stepName").removeClass("is-error");
    $("#stepNameError").addClass("is-hidden");
  }

  function hideApproverErrors() {
    $("#approverUserId").removeClass("is-error");
    $("#thresholdFrom").removeClass("is-error");
    $("#thresholdTo").removeClass("is-error");

    $("#approverUserError").addClass("is-hidden");
    $("#thresholdFromError").addClass("is-hidden");
  }
});