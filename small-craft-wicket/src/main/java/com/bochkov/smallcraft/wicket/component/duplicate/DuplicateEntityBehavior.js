function setupResolveDuplicate(cmpId, messageId, url, message) {
    $('#' + cmpId).closest('.form-group').append("<div class='invalid-feedback'>" + message + ". Для загрузки нажмите <i id='" + messageId + "' class='fa fa-pencil'/></div>");
    $('#' + messageId).click(function () {
        resolveDuplicate(url);
    })
}


function resolveDuplicate(url) {
    Wicket.Ajax.get({'u': url})
}
