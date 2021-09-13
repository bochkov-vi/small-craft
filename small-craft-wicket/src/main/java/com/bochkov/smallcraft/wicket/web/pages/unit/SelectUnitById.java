package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.wicket.select2.data.PersistableChoiceProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

public class SelectUnitById extends Select2Choice<Long> {

    @SpringBean
    UnitRepository repository;

    public SelectUnitById(String id) {
        super(id);
        setProvider(provider());
    }

    public SelectUnitById(String id, IModel<Long> model) {
        super(id, model);
        setProvider(provider());
    }

    @Override
    protected void onInitialize() {
        getSettings().setPlaceholder(getString("unit"))
                .setCloseOnSelect(true)
                .setAllowClear(true)
                .setTheme("bootstrap4");

        super.onInitialize();
    }


    ChoiceProvider<Long> provider() {
        PersistableChoiceProvider<Unit, Long> provider = PersistableChoiceProvider.of(Unit.class, Long.class, () -> this.repository, "name", "id");
        ChoiceProvider<Long> choiceProvider = provider.map();
        return choiceProvider;
    }
}
