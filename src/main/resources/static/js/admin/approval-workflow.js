$(function () {
  let $currentStep = null;

  // ステップ追加モーダルを開く
  $(".js-open-step-modal").on("click", function () {
    $("#stepName").val("");
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

    addStep(stepName);
    refreshStepNumbers();
    closeModal();
  });

  // 承認者追加モーダルを開く
  $(document).on("click", ".js-approver-modal", function () {
    $currentStep = $(this).closest(".workflow-step-block");

    $("#approverUserId").val("");
    $("#selectedApproverEmail").text("");
    $("#thresholdFrom").val("");
    $("#thresholdTo").val("");

    hideApproverErrors();
    openModal("#approverModal");
  });

  // 承認者を選択した時にメールアドレスを補助表示
  $("#approverUserId").on("change", function () {
    const email = $(this).find("option:selected").data("email");
    $("#selectedApproverEmail").text(email);
  });

  $(".js-save-approver").on("click", function () {
    hideApproverErrors();

    const userId = $("#approverUserId").val();
    const thresholdFrom = $("#thresholdFrom").val();

    let hasError = false;

    if (userId === "") {
      $("#approverUserId").addClass("is-error");
      $("#approverUserError")
        .text("承認者を選択してください。")
        .removeClass("is-hidden");
      hasError = true;
    }

    if (thresholdFrom === "") {
      $("#thresholdFrom").addClass("is-error");
      $("#thresholdFromError")
        .text("承認閾値の最小額を入力してください。")
        .removeClass("is-hidden");
      hasError = true;
    }

    if (hasError) {
        return;
    }

    const approver = {
      userId: userId,
      userName: $("#approverUserId option:selected").text(),
      email: $("#approverUserId option:selected").data("email"),
      thresholdFrom: thresholdFrom,
      thresholdTo: $("#thresholdTo").val(),
    };
    addApprover($currentStep, approver);
  });

  /**
   *
   * @param {JQuery<HTMLElement>} $step
   * @param {Object} approver
   */
  function addApprover($step, approver) {
    const $approverList = $step.find(".approver-list");

    const $approverCard = $(`
        <div class="approver-card">
          <button type="button" class="approver-delete-button js-delete-approver">×</button>

          <div class="approver-name"></div>
          <div class="approver-email"></div>
          <div class="approver-threshold"></div>
        </div>
        `);

    $approverCard.attr("data-user-id", approver.userId);
    $approverCard.attr("data-email", approver.email);
    $approverCard.attr("data-threshold-from", approver.thresholdFrom);
    $approverCard.attr("data-threshold-to", approver.thresholdTo);

    $approverCard.find(".approver-name").text(approver.userName);
    $approverCard.find(".approver-email").text(approver.email);

    let thresholdText =
      "承認閾値：" + Number(approver.thresholdFrom).toLocaleString() + "円 ～";
    if (approver.thresholdTo !== "") {
      thresholdText +=
        " " + Number(approver.thresholdTo).toLocaleString() + "円";
    }
    $approverCard.find(".approver-threshold").text(thresholdText);

    $approverList.append($approverCard);
    $approverList.addClass("has-approvers");
    closeModal();
  }

  function refreshStepNumbers() {
    $(".workflow-step-block:visible").each(function (index) {
      $(this)
        .find(".step-number")
        .text(index + 1);
    });
  }

  function addStep(stepName) {
    const $step = $($("#stepTemplate").html());

    $step.find(".step-name").text(stepName);

    $(".workflow-add-step").before($step);
  }

  $(document).on("click", ".js-delete-approver", function () {
    const $approverList = $(this).closest(".approver-list");

    $(this).closest(".approver-card").remove();

    if ($approverList.find(".approver-card").length === 0) {
        $approverList.removeClass("has-approvers");
    }
  })

  // モーダルを閉じる
  $(".js-close-modal").on("click", function () {
    closeModal();
  });

  function openModal(selector) {
    $(selector).removeClass("is-hidden");
  }

  function closeModal() {
    $(".modal-overlay").addClass("is-hidden");
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
    $("#thresholdError").addClass("is-hidden");
  }
});
