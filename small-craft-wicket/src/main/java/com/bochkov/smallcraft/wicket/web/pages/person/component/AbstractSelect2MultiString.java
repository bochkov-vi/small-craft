package com.bochkov.smallcraft.wicket.web.pages.person.component;

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

            @Override
            public String getDisplayValue(String choice) {
                return AbstractSelect2MultiString.this.getDisplayValue(choice);
            }

            @Override
            public String getIdValue(String choice) {
                return AbstractSelect2MultiString.this.getIdValue(choice);
            }

            @Override
            public Collection<String> toChoices(Collection<String> ids) {
                return AbstractSelect2MultiString.this.toChoices(ids);
            }
        };
    }


    public String getDisplayValue(String choice) {
        return choice;
    }


    public String getIdValue(String choice) {
        return choice;
    }


    public Collection<String> toChoices(Collection<String> ids) {
        return ids;
    }

    public abstract void query(String term, int page, Response<String> response);


}
