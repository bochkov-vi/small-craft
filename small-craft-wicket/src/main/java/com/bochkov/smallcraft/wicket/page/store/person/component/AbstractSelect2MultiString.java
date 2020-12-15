package com.bochkov.smallcraft.wicket.page.store.person.component;

import org.apache.wicket.model.IModel;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2MultiChoice;
import org.wicketstuff.select2.StringTextChoiceProvider;

import java.util.Collection;
import java.util.Optional;

public abstract class AbstractSelect2MultiString extends Select2MultiChoice<String> {

    public AbstractSelect2MultiString(String id) {
        super(id);
        setProvider(createProvider());
    }

    public AbstractSelect2MultiString(String id, IModel<Collection<String>> model) {
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
                AbstractSelect2MultiString.this.query(term, page, response);
            }
        };
    }

    public abstract void query(String term, int page, Response<String> response);


}
