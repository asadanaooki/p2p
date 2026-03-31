$(function () {
  $(".js-row-link").on("click", function () {
    const href = $(this).data("href");
    if (href) {
      window.location.href = href;
    }
  });

  $(".js-row-link-a").on("click", function (e) {
    e.stopPropagation();
  });
});
