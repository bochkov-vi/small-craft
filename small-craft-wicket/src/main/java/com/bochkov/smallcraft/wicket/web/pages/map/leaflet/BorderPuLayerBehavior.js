function initBorderPuLayerBehavior(url) {
    $.ajax({
            "url": url,
            "type": "get",
            "success": function (data) {
                const json = JSON.parse(data);
                const layer = L.geoJSON(json);
                layer.addTo(window.map);
            }
        }
    )
}