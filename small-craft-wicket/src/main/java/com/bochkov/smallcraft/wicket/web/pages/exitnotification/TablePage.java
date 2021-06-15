package com.bochkov.smallcraft.wicket.web.pages.exitnotification;

import com.bochkov.bootstrap.tempusdominus.localdatetime.LocalDateTimeTextFieldCalendar;
import com.bochkov.data.jpa.mask.MaskableProperty;
import com.bochkov.hierarchical.Hierarchicals;
import com.bochkov.smallcraft.jpa.entity.AbstractAuditableEntity;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.BaseConverter;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.component.filter.FilterPanel;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.smallcraft.wicket.web.crud.EntityDataTable;
import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeAjaxLink;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.*;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    Boolean longDateTime = false;


    public TablePage(PageParameters parameters) {
        super(ExitNotification.class, parameters);
    }

    public static boolean isLongTime(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return dateFrom != null && dateTo != null && ChronoUnit.DAYS.between(dateFrom.toLocalDate(), dateTo.toLocalDate()) > 0;
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(SmallCraftWebSession.get().getZoneId()).toInstant();
        return instant;
    }

    @Override
    public ExitNotificationRepository getRepository() {
        return repository;
    }

    @Override
    protected EntityDataTable<ExitNotification, Long> createDataTable(String compId) {

        EntityDataTable<ExitNotification, Long> table = super.createDataTable(compId);
        List<AbstractToolbar> toolbars = Lists.newArrayList();
        toolbars.add(new ExitStatToolbar(table, LoadableDetachableModel.of(() -> Optional.ofNullable(unit).flatMap(id -> unitRepository.findById(id)).orElse(null)), ((IModel<Boolean>) () -> includeUnitChilds).orElse(Boolean.FALSE)));
        table.setTopToolbarsFirst(toolbars);
        table.add(new Behavior() {
            @Override
            public void afterRender(Component component) {
                newRowsAjaxTimerBehavior.updateCookie();
            }
        });
        return table;
    }

    @Override
    protected void onInitialize() {
        FilterPanel filterPanel = new FilterPanel("filter", new CompoundPropertyModel<>(this));
        add(filterPanel);
        filterPanel.setOutputMarkupId(true);
        dateFrom = getDefaultDateFrom();
        dateTo = getDefaultDateTo();
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
        FormComponent<Boolean> longDateTimeComponent = new CheckBox("longDateTime");
        longDateTimeComponent.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getPage());
            }
        });
        filterPanel.add(longDateTimeComponent);

        container.setOutputMarkupId(true);
        super.onInitialize();

        newRowsAjaxTimerBehavior = new NewRowsAjaxTimerBehavior(Duration.seconds(10)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                LocalDateTime lmd = repository.findLastModified().map(AbstractAuditableEntity::getModifyDate).orElse(null);
                if (newRowsAjaxTimerBehavior.isNew(lmd)) {
                    target.add(container);
                    target.appendJavaScript("play();");
                }
            }
        };
        add(newRowsAjaxTimerBehavior);

        filterPanel.add(new AjaxLink<Void>("day-forward") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LocalDateTime d1 = dateFrom != null ? dateFrom : getDefaultDateFrom();
                d1 = d1.plusDays(1);
                LocalDateTime d2 = d1.plusDays(1);
                dateTo = d2;
                dateFrom = d1;
                target.add(filterPanel);
                target.add(table);
            }
        }.setBody(new StringResourceModel("day-forward").setParameters(1)).setEscapeModelStrings(false));
        filterPanel.add(new AjaxLink<Void>("month-forward") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LocalDateTime d1 = dateFrom != null ? dateFrom : getDefaultDateFrom();
                d1 = d1.toLocalDate().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
                d1 = d1.plusMonths(1);
                LocalDateTime d2 = d1.plusMonths(1);
                dateTo = d2;
                dateFrom = d1;
                target.add(filterPanel);
                target.add(table);
            }
        }.setBody(new StringResourceModel("month-forward").setParameters(1)).setEscapeModelStrings(false));
        filterPanel.add(new AjaxLink<Void>("day-backward") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LocalDateTime d1 = dateFrom != null ? dateFrom : getDefaultDateFrom();
                d1 = d1.minusDays(1);
                LocalDateTime d2 = d1.plusDays(1);
                dateTo = d2;
                dateFrom = d1;
                target.add(filterPanel);
                target.add(table);
            }
        }.setBody(new StringResourceModel("day-backward").setParameters(1)).setEscapeModelStrings(false));
        filterPanel.add(new AjaxLink<Void>("month-backward") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LocalDateTime d1 = dateFrom != null ? dateFrom : getDefaultDateFrom();
                d1 = d1.toLocalDate().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
                d1 = d1.minusMonths(1);
                LocalDateTime d2 = d1.plusMonths(1);
                dateTo = d2;
                dateFrom = d1;
                target.add(filterPanel);
                target.add(table);
            }
        }.setBody(new StringResourceModel("month-backward").setParameters(1)).setEscapeModelStrings(false));
        filterPanel.add(new AjaxLink<Void>("today") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LocalDateTime d1 = getDefaultDateFrom();
                LocalDateTime d2 = d1.plusDays(1);
                dateTo = d2;
                dateFrom = d1;
                target.add(filterPanel);
                target.add(table);
            }
        }.setBody(new ResourceModel("today")).setEscapeModelStrings(false));
        filterPanel.add(new AjaxLink<Void>("current-month") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LocalDateTime d1 = getDefaultDateFrom().with(TemporalAdjusters.firstDayOfMonth());
                LocalDateTime d2 = d1.plusMonths(1);
                dateTo = d2;
                dateFrom = d1;
                target.add(filterPanel);
                target.add(table);
            }
        }.setBody(new ResourceModel("current-month")).setEscapeModelStrings(false));

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
                } else {
                    fragment.setVisible(true);
                    fragment.add(new AttributeAppender("title", IModel.of(() -> newRowsAjaxTimerBehavior.checked)));
                }
                cellItem.add(fragment);
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("notification.number"), "notification.number", "notification.number"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("id"), "id", en -> BaseConverter.convert(en.getId())) {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> item, String componentId, IModel<ExitNotification> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(new AttributeAppender("title", rowModel.map(ExitNotification::getId)));
            }
        });
        columns.add(new PropertyColumn<ExitNotification, String>(new ResourceModel("captain"), "captain", "captain.fio") {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> item, String componentId, IModel<ExitNotification> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(new AttributeAppender("title", rowModel.map(ExitNotification::getCaptain).map(Person::getFullFio)));
            }
        });
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("phones"), n -> Optional.ofNullable(n.getCaptain()).map(Person::getPhones).map(list -> Joiner.on("; ").join(list)).orElse(null)) {

            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> cellItem, String componentId, IModel<ExitNotification> rowModel) {
                IModel<String> phones = rowModel.map(ExitNotification::getCaptain).map(Person::getPhones).map(list -> String.join("; ", list));
                Label label = new Label(componentId, phones.map(str -> String.format("%s...", StringUtils.substring(str, 0, 17))));
                cellItem.add(label);
                cellItem.add(new AttributeModifier("title", phones));
                cellItem.add(new AttributeModifier("data-toggle", "tooltip"));
                label.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {

                        oldClasses.add("d-block");
                        return oldClasses;
                    }
                });
                label.add(new StyleAttributeModifier() {
                    @Override
                    protected Map<String, String> update(Map<String, String> oldStyles) {
                        oldStyles.put("text-overflow", "ellipsis");
                        //oldStyles.put("display", "block");
                        oldStyles.put("white-space", "nowrap");
                        return oldStyles;
                    }
                });
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("type"), "type", "boat.type"));
        columns.add(new PropertyColumn(new ResourceModel("model"), "model", "boat.model"));
        columns.add(new PropertyColumn(new ResourceModel("tailNumber"), "tailNumber", "boat.tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("pier"), "pier", "pier"));
        columns.add(new LambdaColumn<>(new ResourceModel("regions"), "regions", row -> Optional.ofNullable(row).map(ExitNotification::getRegions).map(set -> set.stream().collect(Collectors.joining("; "))).orElse(null)));
        columns.add(new LambdaColumn<>(new ResourceModel("exitDateTime"), "exitDateTime", en -> format(en.getExitDateTime())));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("estimatedReturnDateTime"), "estimatedReturnDateTime", en -> format(en.getEstimatedReturnDateTime())) {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new AbstractColumn<ExitNotification, String>(new ResourceModel("returnDateTime"), "returnDateTime") {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> cellItem, String componentId, IModel<ExitNotification> rowModel) {
                if (rowModel.map(ExitNotification::getReturnDateTime).map(dt -> true).orElse(false).getObject()) {
                    cellItem.add(new Label(componentId, rowModel.map(ExitNotification::getReturnDateTime).map(rdt -> format(rdt))));
                } else {
                    cellItem.setOutputMarkupId(true);
                    AbstractLink link = new AdminOnlyAjaxLink<ExitNotification>(componentId, PersistableModel.of(rowModel.getObject(), id -> repository.findById(id))) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            target.add(table);
                            ExitNotification exitNotification = getModelObject();
                            exitNotification.setReturnDateTime(LocalDateTime.now(SmallCraftWebSession.get().getZoneId()));
                            exitNotification = repository.save(exitNotification);
                            newRowsAjaxTimerBehavior.updateCookie();
                        }
                    };
                    link.setOutputMarkupId(true);
                    link.setEscapeModelStrings(false);
                    link.setBody(Model.of(String.format("<span class='btn btn-outline-secondary' title='%s'><span class='fa fa-sign-in'></span><span>", getString("setReturnDateTime"))));
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
        columns.add(new PropertyColumn(new ResourceModel("creator"), "creator", "creator"));
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
                Lists.newArrayList("notification.number", "captain.lastName", "boat.tailNumber", "captain.phones", "boat.person.lastName", "boat.person.phones", "boat.registrationNumber", "boat.model")
        ).or((r, q, b) -> {
            Long code = BaseConverter.convert(str);
            return b.equal(r.get("id"), code);
        })).orElse(null));
        where = where.and(Optional.ofNullable(dateTo).map(dt -> (Specification<ExitNotification>) (r, q, b) -> b.lessThanOrEqualTo(r.get("exitDateTime"), dt)).orElse(null));
        if (includeUnitChilds == null || !includeUnitChilds) {
            where = where.and(Optional.ofNullable(unit).map(u -> (Specification<ExitNotification>) (r, q, b) -> r.get("unit").get("id").in(u)).orElse(null));
        } else {
            where = where.and(Optional.ofNullable(unit).flatMap(id -> unitRepository.findById(id)).map(entity -> Hierarchicals.getAllChildIds(true, entity)).map(u -> (Specification<ExitNotification>) (r, q, b) -> r.get("unit").get("id").in(u)).orElse(null));
        }
        Optional<Specification<ExitNotification>> longDateTimeSpecification = Optional.ofNullable(longDateTime).filter(aBoolean -> aBoolean).map(aBoolean -> ExitNotificationRepository.LONG_DATE_TIME_SPECIFICATION);
        where = where.and(longDateTimeSpecification.orElse(null));

        return where;
    }

    @Override
    protected Sort sort() {

        return Sort.by(Sort.Order.desc("modifyDate"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        IRequestHandler handler = new ResourceReferenceRequestHandler(new ContextRelativeResourceReference("res/sound/pristine-609.mp3", false),
                getPageParameters());
        response.render(OnDomReadyHeaderItem.forScript(String.format("window.soundurl='%s'", urlFor(handler))));

        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(TablePage.class, "TablePage.js")));
    }

    @Override
    public void onRowCreated(EntityDataTable<ExitNotification, Long> table, Item<ExitNotification> item, String id, int index, IModel<ExitNotification> model) {
        super.onRowCreated(table, item, id, index, model);
        if (isLongTimeExitNotification(model.getObject())) {
            item.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> oldClasses) {
                    oldClasses.add("long-time");
                    return oldClasses;
                }
            });
        }
    }

    public boolean isLongTimeExitNotification(ExitNotification exitNotification) {
        boolean result = false;
        Optional<ExitNotification> e = Optional.ofNullable(exitNotification);
        result = e.map(n -> isLongTime(n.getExitDateTime(), n.getReturnDateTime())).orElse(false)
                || e.map(n -> n.getReturnDateTime() == null && isLongTime(n.getExitDateTime(), LocalDateTime.now())).orElse(false);
        return result;

    }

    @Override
    public void onEditPageCreated(CrudEditPage<ExitNotification, Long> page) {
        TablePage tablePage = this;
        page.addOnBack(model -> {
            newRowsAjaxTimerBehavior.updateCookie();
            RequestCycle.get().setResponsePage(tablePage);
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        newRowsAjaxTimerBehavior.updateCookie();
    }

    LocalDateTime getDefaultDateFrom() {
        ZoneId zoneId = SmallCraftWebSession.get().getZoneId();
        LocalDateTime localDateTime = LocalDate.now(zoneId).atStartOfDay();
        return localDateTime;
    }

    LocalDateTime getDefaultDateTo() {
        ZoneId zoneId = SmallCraftWebSession.get().getZoneId();
        LocalDateTime localDateTime = LocalDate.now(zoneId).atStartOfDay().plusDays(1);
        return localDateTime;
    }

    static abstract class NewRowsAjaxTimerBehavior extends AbstractAjaxTimerBehavior {

        final static String COOKIE_NAME = "checkTime";

        private Instant checked = Instant.now();

        public NewRowsAjaxTimerBehavior(Duration updateInterval) {
            super(updateInterval);
        }


        @Override
        public void onConfigure(Component component) {
            super.beforeRender(component);
            checked = fromCookie();
        }

        @Override
        protected void onComponentRendered() {
            super.onComponentRendered();
            updateCookie();
        }


        private void updateCookie() {
            checked = Instant.now();
            new CookieUtils().save(COOKIE_NAME, getComponent().getConverter(Long.class).convertToString(checked.toEpochMilli(), Session.get().getLocale()));
        }

        private Instant fromCookie() {
            return Optional.ofNullable(new CookieUtils().load(COOKIE_NAME))
                    .map(cValue -> getComponent().getConverter(Long.class).convertToObject(cValue, Session.get().getLocale()))
                    .map(longValue -> Instant.ofEpochMilli(longValue))
                    .orElseGet(Instant::now);
        }

        public boolean isNew(LocalDateTime lastmd) {
            return Optional.ofNullable(lastmd).map(lmd -> checked.isBefore(toInstant(lmd))).orElse(false);
        }

        public boolean isNew(Instant lastmd) {
            return Optional.ofNullable(lastmd).map(lmd -> checked.isBefore(lmd)).orElse(false);
        }
    }

    @AuthorizeAction(action = Action.RENDER, roles = "ROLE_ADMIN")
    public static abstract class AdminOnlyAjaxLink<X> extends AuthorizeAjaxLink<X> {

        public AdminOnlyAjaxLink(String id, IModel<X> model) {
            super(id, model);
        }
    }
}
