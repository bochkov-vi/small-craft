package com.bochkov.smallcraft.wicket.page.store.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.page.crud.CrudTablePage;
import com.google.common.base.Strings;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Optional;

@MountPath("boat")
public class TablePage extends CrudTablePage<Boat, Long> {

    @Inject
    BoatRepository repository;

    Form form = new Form("form");

    FormComponent<String> searchInput = new TextField<>("search-text", Model.of());

    public TablePage(PageParameters parameters) {
        super(Boat.class, parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form);
        form.add(searchInput);

    }

    @Override
    public BoatRepository getRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn> columns() {
        List<IColumn> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("id"), "id", "id"));
        columns.add(new PropertyColumn(new ResourceModel("registrationNumber"), "registrationNumber", "registrationNumber"));
        columns.add(new PropertyColumn(new ResourceModel("registrationDate"), "registrationDate", "registrationDate"));
        columns.add(new PropertyColumn(new ResourceModel("tailNumber"), "tailNumber", "tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("type"), "type", "type"));
        columns.add(new PropertyColumn(new ResourceModel("model"), "model", "model"));
        columns.add(new PropertyColumn(new ResourceModel("pier"), "pier", "pier"));
        columns.add(new LambdaColumn<Boat, String>(new ResourceModel("person"), "person.lastName", row -> Optional.ofNullable(row).map(Boat::getPerson).map(Person::toString).orElse(null)));
        columns.add(new LambdaColumn<Boat, String>(new ResourceModel("legalPerson"), "legalPerson.name", row -> Optional.ofNullable(row).map(Boat::getLegalPerson).map(Object::toString).orElse(null)));
        columns.add(new PropertyColumn(new ResourceModel("expirationDate"), "expirationDate", "expirationDate"));

        columns.add(new HeaderlessColumn<Boat, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<Boat>> cellItem, String componentId, IModel<Boat> rowModel) {
                Fragment fragment = new Fragment(componentId, "not-link", getPage());
                Link<Boat> link = new Link<Boat>("link", rowModel) {
                    @Override
                    public void onClick() {
                        PageParameters parameters = new PageParameters();
                        parameters.set("boat", getConverter(Boat.class).convertToString(getModelObject(), Session.get().getLocale()));
                        com.bochkov.smallcraft.wicket.page.store.notification.EditPage notificationPage = new com.bochkov.smallcraft.wicket.page.store.notification.EditPage(parameters);
                        notificationPage.setBackPage(getPage());
                        setResponsePage(notificationPage);
                    }
                };
                cellItem.add(fragment);
                fragment.add(link);
            }
        });
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<? extends CrudEditPage<Boat, Long>> getEditPageClass() {
        return EditPage.class;
    }

    @Override
    protected Specification<Boat> specification() {
        List<Specification<Boat>> list = Lists.newArrayList();
        String search = searchInput.getModelObject();
        if (search != null && !Strings.isNullOrEmpty(search)) {
            list.add((r, q, b) -> b.like(b.lower(r.get("registrationNumber").as(String.class)), "%" + search.toLowerCase() + "%"));
            list.add((r, q, b) -> b.like(b.lower(r.get("person").get("lastName").as(String.class)), "%" + search.toLowerCase() + "%"));
            list.add((r, q, b) -> {
                Join<Boat, LegalPerson> lp = r.join("legalPerson", JoinType.LEFT);
                return b.like(b.lower(lp.get("name").as(String.class)), "%" + search.toLowerCase() + "%");
            });
        }

        Specification<Boat> result = null;
        for (Specification<Boat> s : list) {
            if (result == null) {
                result = s;
            } else {
                result = result.or(s);
            }
        }
        return result;
    }
}
