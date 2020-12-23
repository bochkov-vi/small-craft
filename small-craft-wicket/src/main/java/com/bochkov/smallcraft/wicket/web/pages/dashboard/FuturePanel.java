package com.bochkov.smallcraft.wicket.web.pages.dashboard;

import org.apache.poi.ss.formula.functions.T;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.model.IModel;

import java.util.concurrent.Future;

public class FuturePanel<C extends Component> extends AjaxLazyLoadPanel<C> {

    Future<T> future;

    public FuturePanel(String id) {
        super(id);
    }

    public FuturePanel(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    public C getLazyLoadComponent(String markupId) {
        return null;
    }

}
