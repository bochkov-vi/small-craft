package com.bochkov.smallcraft.wicket.web.pages.dashboard;

import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.wicket.web.BasePage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

@MountPath("dashboard")
public class DashBoardPage extends BasePage<LocalDate> {

    @Inject
    BoatRepository boatRepository;

    public DashBoardPage() {
        super(Model.of(LocalDate.now()));
    }

    public DashBoardPage(PageParameters parameters) {
        super(parameters);
        LocalDate date = Optional.ofNullable(parameters.get(0).toOptionalString()).map(str -> getConverter(LocalDate.class).convertToObject(str, getSession().getLocale())).orElse(null);
        if (date == null) {
            date = Optional.ofNullable(parameters.get("date").toOptionalString()).map(str -> getConverter(LocalDate.class).convertToObject(str, getSession().getLocale())).orElse(null);
        }
        setModel(Model.of(date));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new DashBoardPanel("dashboard", getModel()));
        /*add(new Label("boat-total", LoadableDetachableModel.of(() -> boatRepository.count((r, q, b) -> {
            return b.and(
                    b.lessThanOrEqualTo(r.get("registrationDate"), getModelObject()),
                    b.or(b.greaterThanOrEqualTo(r.get("expirationDate"), getModelObject()), r.get("expirationDate").isNull()));
        }))));*/
    }
}
