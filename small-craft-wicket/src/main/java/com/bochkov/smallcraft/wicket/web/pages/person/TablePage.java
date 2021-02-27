package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.data.jpa.mask.Maskable;
import com.bochkov.data.jpa.mask.MaskableProperty;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.google.common.collect.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@MountPath("person")
public class TablePage extends CrudTablePage<Person, Long> {

    @Inject
    PersonRepository repository;

    IModel<String> search = Model.of();

    Form form = new Form<Void>("form");

    public TablePage(PageParameters parameters) {
        super(Person.class, parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form);
        form.add(new TextField("search-text", search));
    }


    @Override
    protected Specification<Person> specification() {
        Specification specification = search.map(str -> MaskableProperty.maskSpecification(str,
                Lists.newArrayList("lastName", "passport.number", "phones", "email", "address"))).orElse(null).getObject();
        return specification;
    }

    @Override
    public PersonRepository getRepository() {
        return repository;
    }

    @Override
    protected List<? extends IColumn<Person, String>> columns() {
        List<IColumn<Person,String>> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("id"), "id", "id"));

        columns.add(new LambdaColumn<Person, String>(new ResourceModel("lastName"), "lastName", Person::toString));
        columns.add(new PropertyColumn(new ResourceModel("passport.serial"), "passport.serial", "passport.serial"));
        columns.add(new PropertyColumn(new ResourceModel("passport.number"), "passport.number", "passport.number"));
        columns.add(new PropertyColumn(new ResourceModel("passport.date"), "passport.date", "passport.date"));
        columns.add(new PropertyColumn(new ResourceModel("passport.data"), "passport.data", "passport.data"));
        columns.add(new LambdaColumn<Person, String>(new ResourceModel("phones"), "phones", p -> p.getPhones().stream().collect(Collectors.joining("; "))));
        columns.add(new PropertyColumn(new ResourceModel("address"), "address", "address"));
        columns.add(new PropertyColumn(new ResourceModel("birthDate"), "birthDate", "birthDate"));
        columns.add(new PropertyColumn(new ResourceModel("email"), "email", "email"));
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<? extends CrudEditPage<Person, Long>> getEditPageClass() {
        return EditPage.class;
    }


}
