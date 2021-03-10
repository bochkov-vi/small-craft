function scrollToAnchor(aid) {
    const aTag = $("[name='" + aid + "']");
    aTag.closest("tr").css("box-shadow", "0 0 30px #44f");
    const top = aTag.offset().top - 100;
    $('html,body').animate({scrollTop: top}, 'slow');

}