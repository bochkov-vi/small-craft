package com.bochkov.smallcraft.wicket.web.pages.exitnotification;

import com.bochkov.bootstrap.tempusdominus.localdatetime.LocalDateTimeTextFieldCalendar;
import com.bochkov.data.jpa.mask.MaskableProperty;
import com.bochkov.hierarchical.Hierarchicals;
import com.bochkov.smallcraft.jpa.entity.AbstractAuditableEntity;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.component.filter.FilterPanel;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.giffing.wicket.spring.boot.starter.web.servlet.websocket.WebSocketMessageBroadcaster;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.cookies.CookieUtils;
import org.apache.wicket.util.time.Duration;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@MountPath("exit-notification")
public class TablePage extends CrudTablePage<ExitNotification, Long> {

    @Inject
    ExitNotificationRepository repository;

    @Inject
    UnitRepository unitRepository;


    Boolean onExitOnly;

    LocalDateTime dateFrom;

    LocalDateTime dateTo;

    Long unit;

    String quickSearch;

    NewRowsAjaxTimerBehavior newRowsAjaxTimerBehavior;

    Boolean includeUnitChilds = true;

    public TablePage(PageParameters parameters) {
        super(ExitNotification.class, parameters);
    }

    @Override
    public ExitNotificationRepository getRepository() {
        return repository;
    }

    @Override
    protected void onInitialize() {
        FilterPanel filterPanel = new FilterPanel("filter", new CompoundPropertyModel<>(this));
        add(filterPanel);
        dateFrom = LocalDateTime.from(LocalDate.now().atStartOfDay(SmallCraftWebSession.get().getZoneId()));
        dateTo = dateFrom.plusDays(1);
        filterPanel.add(new LocalDateTimeTextFieldCalendar("dateFrom", getString("dateTimeFormat")));
        filterPanel.add(new LocalDateTimeTextFieldCalendar("dateTo", getString("dateTimeFormat")));

        FormComponent<Boolean> onExitFormComponent = new CheckBox("onExitOnly");
        onExitFormComponent.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getPage());
            }
        });
        filterPanel.add(onExitFormComponent);

        container.setOutputMarkupId(true);
        super.onInitialize();
        newRowsAjaxTimerBehavior = new NewRowsAjaxTimerBehavior(Duration.seconds(10)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                LocalDateTime lmd = repository.findLastModified().map(AbstractAuditableEntity::getModifyDate).orElse(null);
                if (newRowsAjaxTimerBehavior.isNew(lmd)) {
                    target.add(container);
                }
            }
        };
        add(newRowsAjaxTimerBehavior);
    }

    @Override
    protected List<? extends IColumn<ExitNotification, String>> columns() {
        List<IColumn<ExitNotification, String>> columns = Lists.newArrayList();
        columns.add(new HeaderlessColumn<ExitNotification, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> cellItem, String componentId, IModel<ExitNotification> rowModel) {
                Fragment fragment = new Fragment(componentId, "badge-new", getPage());
                if (!newRowsAjaxTimerBehavior.isNew(rowModel.map(AbstractAuditableEntity::getModifyDate).orElse(null).getObject())) {
                    fragment.setVisible(false);
                }else {
                    fragment.setVisible(true);
                    fragment.add(new AttributeAppender("title",IModel.of(()->newRowsAjaxTimerBehavior.checked)));
                }
                cellItem.add(fragment);
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("notification.number"), "notification.number", "notification.number"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("id"), "id", en -> repository.convert(en.getId())));
        columns.add(new PropertyColumn(new ResourceModel("captain"), "captain", "captain.fio"));
        columns.add(new AbstractColumn<ExitNotification, String>(new ResourceModel("phones")) {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> cellItem, String componentId, IModel<ExitNotification> rowModel) {
                IModel<String> phones = rowModel.map(ExitNotification::getCaptain).map(Person::getPhones).map(list -> String.join("; ", list));
                cellItem.add(new Label(componentId, phones.map(str -> String.format("%s...", StringUtils.substring(str, 0, 20)))));
                cellItem.add(new AttributeModifier("title", phones));
                cellItem.add(new AttributeModifier("data-toggle", "tooltip"));
            }
        });
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
        columns.add(new AbstractColumn<ExitNotification, String>(new ResourceModel("returnDateTime"), "returnDateTime") {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> cellItem, String componentId, IModel<ExitNotification> rowModel) {
                if (rowModel.map(ExitNotification::getReturnDateTime).map(dt -> true).orElse(false).getObject()) {
                    cellItem.add(new Label(componentId, rowModel.map(ExitNotification::getReturnDateTime)));
                } else {
                    cellItem.setOutputMarkupId(true);
                    AbstractLink link = new AjaxLink<ExitNotification>(componentId, rowModel) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            target.add(cellItem);
                            ExitNotification exitNotification = rowModel.getObject();
                            exitNotification.setReturnDateTime(LocalDateTime.now(SmallCraftWebSession.get().getZoneId()));
                            exitNotification = repository.save(exitNotification);
                            rowModel.setObject(exitNotification);
                        }
                    };
                    link.setOutputMarkupId(true);
                    link.setEscapeModelStrings(false);
                    link.setBody(Model.of("<span class='btn btn-outline-secondary'><span class='fa fa-sign-in'></span><span>"));
                    cellItem.add(link);
                }
            }
        });
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
        where = where.and(Optional.ofNullable(quickSearch).map(str -> MaskableProperty.maskSpecification(str,
                Lists.newArrayList("notification.number", "captain.lastName", "boat.tailNumber", "captain.phones", "boat.registrationNumber")
        )).orElse(null));
        where = where.and(Optional.ofNullable(dateTo).map(dt -> (Specification<ExitNotification>) (r, q, b) -> b.lessThanOrEqualTo(r.get("exitDateTime"), dt)).orElse(null));
        if (includeUnitChilds == null || !includeUnitChilds) {
            where = where.and(Optional.ofNullable(unit).map(u -> (Specification<ExitNotification>) (r, q, b) -> r.get("unit").get("id").in(u)).orElse(null));
        } else {
            where = where.and(Optional.ofNullable(unit).flatMap(id -> unitRepository.findById(id)).map(entity -> Hierarchicals.getAllChildIds(true, entity)).map(u -> (Specification<ExitNotification>) (r, q, b) -> r.get("unit").get("id").in(u)).orElse(null));
        }
        return where;
    }

    @Override
    protected Sort sort() {

        return Sort.by(Sort.Order.desc("modifyDate"));
    }


    static abstract class NewRowsAjaxTimerBehavior extends AbstractAjaxTimerBehavior {

        final static String COOKIE_NAME = "checkTime";

        LocalDateTime checked = LocalDateTime.now();

        public NewRowsAjaxTimerBehavior(Duration updateInterval) {
            super(updateInterval);
        }


        @Override
        public void onConfigure(Component component) {
            super.beforeRender(component);
            checked = Optional.ofNullable(new CookieUtils().load(COOKIE_NAME))
                    .map(cValue -> getComponent().getConverter(Long.class).convertToObject(cValue, Session.get().getLocale()))
                    .map(longValue -> LocalDateTime.ofInstant(Instant.ofEpochMilli(longValue), ZoneId.systemDefault()))
                    .orElseGet(LocalDateTime::now);
        }

        @Override
        protected void onComponentRendered() {
            super.onComponentRendered();
            new CookieUtils().save(COOKIE_NAME, getComponent().getConverter(Long.class).convertToString(System.currentTimeMillis(), Session.get().getLocale()));
        }

        public boolean isNew(LocalDateTime lastmd) {
            return Optional.ofNullable(lastmd).map(lmd -> lmd.isAfter(checked)).orElse(false);
        }
    }

}
