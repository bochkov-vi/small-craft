package com.bochkov.smallcraft.wicket.web.pages.map.leaflet;

import org.apache.wicket.Component;
import org.apache.wicket.IRequestListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class BorderPuLayerBehavior extends Behavior implements IRequestListener {

    Map map;

    @Override
    public void bind(Component component) {
        super.bind(component);
        map = (Map) component;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "BorderPuLayerBehavior.js")));
        response.render(OnDomReadyHeaderItem.forScript("initBorderPuLayerBehavior('" + component.urlForListener(this, null) + "')"));
    }

    @Override
    public void onRequest() {
        WebResponse webResponse = (WebResponse) RequestCycle.get().getResponse();
        webResponse.setContentType("application/json");
        String data = generateJSON();
        OutputStreamWriter out = new OutputStreamWriter(webResponse.getOutputStream());
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String generateJSON() {
        return "[\"{\\\"type\\\":\\\"Feature\\\",\\\"geometry\\\":{\\\"type\\\":\\\"LineString\\\",\\\"coordinates\\\":[[52.57,158.32],[53.57,158.32],[53.57,159.32],[52.57,159.32]]},\\\"properties\\\":{\\\"feat_id\\\":11}}\"]";
    }

    @Override
    public boolean rendersPage() {
        return false;
    }
}
