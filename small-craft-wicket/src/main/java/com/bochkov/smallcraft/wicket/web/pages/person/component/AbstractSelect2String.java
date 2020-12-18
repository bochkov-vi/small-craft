package com.bochkov.smallcraft.wicket.web.pages.person.component;

import org.apache.wicket.model.IModel;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2Choice;
import org.wicketstuff.select2.StringTextChoiceProvider;

import java.util.Optional;

public abstract class AbstractSelect2String extends Select2Choice<String> {

    public AbstractSelect2String(String id) {
        super(id);
        setProvider(createProvider());
    }

    public AbstractSelect2String(String id, IModel<String> model) {
        super(id, model);
        setProvider(createProvider());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        getSettings().setTheme("bootstrap4").setPlaceholder(Optional.ofNullable(getDefaultLabel()).orElse(""));
        getSettings().setCloseOnSelect(true);
        getSettings().setAllowClear(true);
    }

    private ChoiceProvider<String> createProvider() {
        return new StringTextChoiceProvider() {
            @Override
            public void query(String term, int page, Response<String> response) {
                AbstractSelect2String.this.query(term, page, response);
            }
        };
    }

    public abstract void query(String term, int page, Response<String> response);


}
