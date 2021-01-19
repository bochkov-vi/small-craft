package com.bochkov.smallcraft.wicket.web.pages.map.leaflet;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

public class Map extends GenericPanel<Void> {

    public Map(String id) {
        super(id);
    }

    public Map(String id, IModel<Void> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        setOutputMarkupId(true);
        add(new BorderPuLayerBehavior());
        super.onInitialize();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(Map.class, "js/leaflet.js")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(Map.class, "Map.js")));

        response.render(CssHeaderItem.forReference(new PackageResourceReference(Map.class, "js/leaflet.css")));
        response.render(OnDomReadyHeaderItem.forScript(String.format("initMap('%s')", getMarkupId())));
        super.renderHead(response);
    }
}
