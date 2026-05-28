$(function () {
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

  function refreshStepNumbers() {
    $(".workflow-step-block:visible").each(function (index) {
        $(this).find(".step-number").text(index + 1);
    })
  }

  function addStep(stepName) {
    const $step = $($("#stepTemplate").html());

    $step.find(".step-name").text(stepName);

    $(".workflow-add-step").before($step);
  }

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
});
