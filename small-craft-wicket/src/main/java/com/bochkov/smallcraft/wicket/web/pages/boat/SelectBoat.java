package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.wicket.select2.data.MaskableChoiceProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

public class SelectBoat extends Select2Choice<Boat> {

    @SpringBean
    BoatRepository repository;

    public SelectBoat(String id) {
        super(id);
        setProvider(provider());
    }

    public SelectBoat(String id, IModel<Boat> model) {
        super(id, model);
        setProvider(provider());
    }

    @Override
    protected void onInitialize() {
        getSettings().setPlaceholder(getString("boat")).setCloseOnSelect(true).setAllowClear(true).setTheme("bootstrap4");
        super.onInitialize();
    }

    ChoiceProvider<Boat> provider() {
        ChoiceProvider<Boat> provider = MaskableChoiceProvider.of(Boat.class, (s, p) -> repository.findAll(s, p), "tailNumber","id", "model");
        //ChoiceProvider<Boat> provider = PersistableChoiceProvider.of(Boat.class, (s, p) -> repository.findAll(s, p), "lastName", "legalBoat.name");
        return provider;
    }

}
