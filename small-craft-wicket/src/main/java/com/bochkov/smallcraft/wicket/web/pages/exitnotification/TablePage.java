package com.bochkov.smallcraft.wicket.web.pages.exitnotification;

import com.bochkov.data.jpa.mask.Maskable;
import com.bochkov.hierarchical.Hierarchicals;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.component.localdate.LocalDateTextFieldCalendar;
import com.bochkov.smallcraft.wicket.component.localdatetime.LocalDateTimeTextFieldCalendar;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnitById;
import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@MountPath("exit-notification")
public class TablePage extends CrudTablePage<ExitNotification, Long> {

    @Inject
    ExitNotificationRepository repository;

    @Inject
    UnitRepository unitRepository;

    Form form = new Form<Void>("form");


    Boolean onExitOnly;

    LocalDateTime dateFrom;

    LocalDateTime dateTo;

    LocalDate currentDate;

    String quickSearch;

    Long unit;

    Boolean unitIncludeChilds;

    public TablePage(PageParameters parameters) {
        super(ExitNotification.class, parameters);
    }

    @Override
    public ExitNotificationRepository getRepository() {
        return repository;
    }

    @Override
    protected void onInitialize() {


        currentDate = LocalDate.now(SmallCraftWebSession.get().getZoneId());
        dateFrom = LocalDateTime.from(currentDate.atStartOfDay(SmallCraftWebSession.get().getZoneId()));
        dateTo = dateFrom.plusDays(1);
        add(form);
        form.setModel(new CompoundPropertyModel(this));
        form.add(new LocalDateTimeTextFieldCalendar("dateFrom", getString("dateTimeFormat")));
        form.add(new LocalDateTimeTextFieldCalendar("dateTo", getString("dateTimeFormat")));
        form.add(new LocalDateTextFieldCalendar("currentDate", getString("dateFormat")).setVisible(false));
        form.add(new TextField<>("quickSearch"));
        form.add(new SessionSelectUnitById("unit"));
        form.add(new CheckBox("unitIncludeChilds").setOutputMarkupId(true));
        FormComponent<Boolean> onExitFormComponent = new CheckBox("onExitOnly");
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
                onExitOnly = null;
                dateFrom = null;
                dateTo = null;
                currentDate = null;
                quickSearch = null;
            }
        });
        super.onInitialize();
    }

    @Override
    protected List<? extends IColumn<ExitNotification,String>> columns() {
        List<IColumn<ExitNotification,String>> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("notification.number"), "notification.number", "notification.number"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("id"), "id", en -> repository.convert(en.getId())));
        columns.add(new PropertyColumn(new ResourceModel("captain"), "captain", "captain.fio"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("phones"), row -> Optional.ofNullable(row.getCaptain()).map(Person::getPhones).map(list -> list.stream().collect(Collectors.joining("; "))).orElse(null)));
        columns.add(new PropertyColumn(new ResourceModel("type"), "type", "boat.type"));
        columns.add(new PropertyColumn(new ResourceModel("model"), "model", "boat.model"));
        columns.add(new PropertyColumn(new ResourceModel("tailNumber"), "tailNumber", "boat.tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("pier"), "pier", "pier"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("regions"), "regions", row -> Optional.ofNullable(row).map(ExitNotification::getRegions).map(set -> set.stream().collect(Collectors.joining("; "))).orElse(null)));
        columns.add(new PropertyColumn(new ResourceModel("exitDateTime"), "exitDateTime", "exitDateTime"));
        columns.add(new PropertyColumn(new ResourceModel("estimatedReturnDateTime"), "estimatedReturnDateTime", "estimatedReturnDateTime") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("returnDateTime"), "returnDateTime", "returnDateTime"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("unit"), "unit.name", row -> Optional.ofNullable(row).map(ExitNotification::getUnit).map(Unit::getName).orElse(null)) {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });

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
        where = where.and(Optional.ofNullable(onExitOnly).filter(bol -> bol).map(bol -> (Specification<ExitNotification>) (r, q, b) -> r.get("returnDateTime").isNull()).orElse(null));
       /* where = where.and(Optional.ofNullable(currentDate).map(d -> new LocalDateTime[]{d.atStartOfDay(), d.atStartOfDay().plusDays(1)})
                .map(arr -> (Specification<ExitNotification>) (r, q, b) -> b.and(b.lessThanOrEqualTo(r.get("exitDateTime"), arr[1]),
                        b.or(b.greaterThanOrEqualTo(r.get("returnDateTime"), arr[0]),
                                r.get("returnDateTime").isNull()))).orElse(null));
*/        //date from
        where = where.and(Optional.ofNullable(dateFrom).map(df -> (Specification<ExitNotification>) (r, q, b) -> b.or(
                b.greaterThanOrEqualTo(r.get("returnDateTime"), df),
                r.get("returnDateTime").isNull())
        ).orElse(null));
        where = where.and(Optional.ofNullable(quickSearch).map(str -> Maskable.maskSpecification(str,
                Lists.newArrayList("notification.number", "captain.lastName", "boat.tailNumber", "captain.phones", "boat.registrationNumber")
        )).orElse(null));
        where = where.and(Optional.ofNullable(dateTo).map(dt -> (Specification<ExitNotification>) (r, q, b) -> b.lessThanOrEqualTo(r.get("exitDateTime"), dt)).orElse(null));
        if (unitIncludeChilds == null || !unitIncludeChilds) {
            where = where.and(Optional.ofNullable(unit).map(u -> (Specification<ExitNotification>) (r, q, b) -> r.get("unit").get("id").in(u)).orElse(null));
        } else {
            where = where.and(Optional.ofNullable(unit).flatMap(id -> unitRepository.findById(id)).map(entity -> Hierarchicals.getAllChildIds(true, entity)).map(u -> (Specification<ExitNotification>) (r, q, b) -> r.get("unit").get("id").in(u)).orElse(null));
        }
        return where;
    }

    @Override
    protected Sort sort() {
        return Sort.by("id");
    }
}
