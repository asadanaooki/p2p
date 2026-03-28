$(function () {
  const $filterPanel = $("#filterPanel");
  const $toggleButton = $("#filterToggleButton");
  const $keywordForm = $('.keyword-search-form');
  const $keywordSortBy = $keywordForm.find("#sortBy");
  const $keywordSortDirection = $keywordForm.find("#sortDirection");

  function syncFilterState() {
    if ($filterPanel.is(":visible")) {
      $toggleButton.addClass("is-open");
    } else {
      $toggleButton.removeClass("is-open");
    }
  }

  syncFilterState();

  $toggleButton.on("click", function () {
    $filterPanel.slideToggle(250, function () {
      syncFilterState();
    });
  });

  $('.sort-button').on('click', function() {
    const clickedSortBy = $(this).data('sort-by');
    const currentSortBy = $keywordSortBy.val();
    const currentSortDirection = $keywordSortDirection.val();

    if (currentSortBy === clickedSortBy) {
        $keywordSortDirection
        .val(currentSortDirection === 'ASC' ? 'DESC' : 'ASC');
    } else{
        $keywordSortBy.val(clickedSortBy);
        $keywordSortDirection.val('ASC');
    }

    $keywordForm[0].submit();
  })

});
