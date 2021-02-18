function scrollToAnchor(aid) {
    var aTag = $("[name='" + aid + "']");
    $('html,body').animate({scrollTop: aTag.offset().top}, 'slow');
}