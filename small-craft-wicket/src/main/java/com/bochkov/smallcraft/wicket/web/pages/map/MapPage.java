package com.bochkov.smallcraft.wicket.web.pages.map;

import com.bochkov.smallcraft.wicket.web.BasePage;
import com.bochkov.smallcraft.wicket.web.pages.map.leaflet.Map;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("map")
public class MapPage extends BasePage<Void> {

    public MapPage() {
    }

    public MapPage(IModel<Void> model) {
        super(model);
    }

    public MapPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Map("map", getModel()));
    }
}
