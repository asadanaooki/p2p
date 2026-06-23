$(function () {
  /* =========================
     フィルター開閉・ソート用要素
  ========================= */
  const $filterPanel = $("#filterPanel");
  const $toggleButton = $("#filterToggleButton");
  const $keywordForm = $(".keyword-search-form");
  const $keywordSortBy = $keywordForm.find("#sortBy");
  const $keywordSortDirection = $keywordForm.find("#sortDirection");

  /* =========================
     フィルターボタン状態同期
  ========================= */
  function syncFilterState() {
    if ($filterPanel.length === 0 || $toggleButton.length === 0) {
      return;
    }

    if ($filterPanel.is(":visible")) {
      $toggleButton.addClass("is-open");
    } else {
      $toggleButton.removeClass("is-open");
    }
  }

  syncFilterState();

  /* =========================
     フィルターパネル開閉
  ========================= */
  $toggleButton.on("click", function () {
    $filterPanel.slideToggle(250, function () {
      syncFilterState();
    });
  });

  /* =========================
     列ソート
  ========================= */
  $(".sort-button").on("click", function () {
    if ($keywordForm.length === 0) {
      return;
    }

    const clickedSortBy = $(this).data("sort-by");
    const currentSortBy = $keywordSortBy.val();
    const currentSortDirection = $keywordSortDirection.val();

    if (currentSortBy === clickedSortBy) {
      $keywordSortDirection.val(
        currentSortDirection === "ASC" ? "DESC" : "ASC",
      );
    } else {
      $keywordSortBy.val(clickedSortBy);
      $keywordSortDirection.val("ASC");
    }

    $keywordForm[0].submit();
  });

  /* =========================
   ページング
  ========================= */
  $(".js-page").on("click", function () {
    if ($keywordForm.length === 0) {
        return;
    }

    $keywordForm.find('input[name="page"]').remove();

    appendHidden($keywordForm, "page", $(this).data("page"));
    
    $keywordForm[0].submit();
  })

  /* =========================
     行全体クリック遷移
  ========================= */
  $(".js-row-link").on("click", function () {
    const href = $(this).data("href");
    if (href) {
      window.location.href = href;
    }
  });

  /* =========================
     行クリック内リンクは親遷移を止める
  ========================= */
  $(".js-row-link-a").on("click", function (e) {
    e.stopPropagation();
  });
});

function formatNumber(value) {
  return Number(value).toLocaleString("ja-JP");
}

function appendHidden($container, name, value) {
  $("<input>")
    .attr("type", "hidden")
    .attr("name", name)
    .val(value)
    .appendTo($container);
}
