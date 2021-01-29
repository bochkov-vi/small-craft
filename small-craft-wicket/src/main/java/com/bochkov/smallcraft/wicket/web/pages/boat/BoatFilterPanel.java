package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import com.bochkov.wicket.data.model.PersistableModel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class BoatFilterPanel extends GenericPanel<Void> {

    @SpringBean
    UnitRepository unitRepository;

    String quickSearch;

    IModel<Unit> unit = PersistableModel.of(id -> unitRepository.findById(id));

    Expirated expire = null;

    public BoatFilterPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        Form form = new Form<>("form", new CompoundPropertyModel<>(this));
        add(form);
        form.add(new TextField<>("quickSearch", String.class));
        form.add(new SessionSelectUnit("unit",unit));
        FormComponent<Expirated> expiratedDropDownChoice = new DropDownChoice<>("expire", Lists.newArrayList(Expirated.values()), new EnumChoiceRenderer<>(getPage())).setNullValid(true);
        form.add(expiratedDropDownChoice);
        super.onInitialize();
    }


    public Specification<Boat> specification() {
        Specification<Boat> specification = Specification.where(null);
        if (quickSearch != null && !Strings.isNullOrEmpty(quickSearch)) {
            specification = specification.or((r, q, b) -> b.like(b.lower(r.get("registrationNumber").as(String.class)), "%" + quickSearch.toLowerCase() + "%"));
            specification = specification.or((r, q, b) -> b.like(b.lower(r.get("tailNumber").as(String.class)), "%" + quickSearch.toLowerCase() + "%"));
            specification = specification.or((r, q, b) -> b.like(b.lower(r.get("model").as(String.class)), "%" + quickSearch.toLowerCase() + "%"));
            specification = specification.or((r, q, b) -> b.like(b.lower(r.join("person", JoinType.LEFT).get("lastName").as(String.class)), "%" + quickSearch.toLowerCase() + "%"));
            specification = specification.or((r, q, b) -> {
                Join<Boat, LegalPerson> lp = r.join("legalPerson", JoinType.LEFT);
                return b.like(b.lower(lp.get("name").as(String.class)), "%" + quickSearch.toLowerCase() + "%");
            });
        }
        if (unit != null && unit.getObject() != null) {
            specification = specification.and(
                    (r, q, b) -> r.join("unit", JoinType.LEFT).in(unit.map(Unit::getAllChildsAndThis).getObject()));
        }
        if (expire != null) {
            switch (expire) {
                case NOT_EXPIRATED: {
                    specification = specification.and((r, q, b) -> r.get("expirationDate").isNull());
                    break;
                }
                case EXPIRATED: {
                    specification = specification.and((r, q, b) -> r.get("expirationDate").isNotNull());
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return specification;
    }

    public enum Expirated {
        EXPIRATED, NOT_EXPIRATED
    }
}
