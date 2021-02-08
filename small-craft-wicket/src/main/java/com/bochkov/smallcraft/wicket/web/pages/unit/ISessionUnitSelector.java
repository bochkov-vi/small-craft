package com.bochkov.smallcraft.wicket.web.pages.unit;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.Optional;

public interface ISessionUnitSelector {

    public default void setAllFormModels(Long unit, Form form) {
        form.visitFormComponents(new IVisitor<FormComponent<?>, Object>() {
            @Override
            public void component(FormComponent<?> cmp, IVisit<Object> visit) {
                if (cmp instanceof ISessionUnitSelector) {
                    ((ISessionUnitSelector) cmp).setIdUnitToModel(unit);
                }
            }
        });
        setIdUnitToSession(unit);
    }

    default Optional<Long> getIdUnitFromSession() {
        return Optional.ofNullable((Long) Session.get().getAttribute("id_unit"));
    }

    default void setIdUnitToSession(Long idUnit) {
        Session.get().setAttribute("id_unit", idUnit);
    }

    public void setIdUnitToModel(Long idUnit);
}
