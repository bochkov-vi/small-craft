package com.bochkov.smallcraft.wicket.page.store.exitnotification;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.page.crud.CrudTablePage;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@MountPath("exit-notification")
public class TablePage extends CrudTablePage<ExitNotification, Long> {

    @Inject
    ExitNotificationRepository repository;

    Form form = new Form<Void>("form");

    FormComponent<Boolean> onExitFormComponent = new CheckBox("boat-onexit", Model.of(false));

    public TablePage(PageParameters parameters) {
        super(ExitNotification.class, parameters);
    }

    @Override
    public ExitNotificationRepository getRepository() {
        return repository;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form);
        onExitFormComponent.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getPage());
            }
        });
        form.add(onExitFormComponent);
        form.add(new Button("clear-filter") {
            @Override
            public void onSubmit() {
                form.clearInput();
                onExitFormComponent.setModelObject(false);
            }
        });
    }

    @Override
    protected List<? extends IColumn> columns() {
        List<IColumn> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("id"), "id", "id"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("boat"), "boat", n -> Optional.ofNullable(n).map(ExitNotification::getBoat).map(Boat::toString).orElse(null)));
        columns.add(new PropertyColumn(new ResourceModel("pier"), "pier", "pier"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("region"), "region", row -> Optional.ofNullable(row).map(ExitNotification::getRegion).map(set -> set.stream().collect(Collectors.joining("; "))).orElse(null)));
        columns.add(new PropertyColumn(new ResourceModel("captain"), "captain", "captain.fio"));
        columns.add(new PropertyColumn(new ResourceModel("exitDateTime"), "exitDateTime", "exitDateTime"));
        columns.add(new PropertyColumn(new ResourceModel("returnDateTime"), "returnDateTime", "returnDateTime"));
//        columns.add(new PropertyColumn(new ResourceModel("activity"), "activity", "activity"));

        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<? extends CrudEditPage<ExitNotification, Long>> getEditPageClass() {
        return EditPage.class;
    }

    @Override
    protected Specification<ExitNotification> specification() {
        Specification where = Specification.where(super.specification());
        where = where.and(onExitFormComponent.getModel().filter(bol -> bol).map(bol -> (Specification<ExitNotification>) (r, q, b) -> r.get("returnDateTime").isNull()).getObject());
        return where;
    }
}
