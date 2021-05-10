package com.bochkov.smallcraft.wicket.web.pages.dashboard;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.web.pages.boat.TablePage;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import com.bochkov.wicket.component.table.XLSXDataExportLink;
import com.bochkov.wicket.data.provider.ListModelDataProvider;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.springframework.data.jpa.domain.Specification;

import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DashBoardPanel extends GenericPanel<LocalDate> {

    @Inject
    BoatRepository boatRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    ExitNotificationRepository exitNotificationRepository;

    @Inject
    UnitRepository unitRepository;

    SessionSelectUnit unit = new SessionSelectUnit("unit", PersistableModel.of(id -> unitRepository.findById(id)));

    FormComponent<Boolean> includeUnitChilds = new CheckBox("includeUnitChilds", Model.of(true));

    public DashBoardPanel(String id) {
        super(id);
    }

    public DashBoardPanel(String id, IModel<LocalDate> model) {
        super(id, model);
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form form = new Form<Void>("form");
        form.add(unit, includeUnitChilds);
        add(form);

        IDataProvider provider = new ListModelDataProvider<RowData>(() -> createData()) {
            @Override
            public IModel<RowData> model(RowData object) {
                return Model.of(object);
            }

        };
        DataTable table = new DataTable<RowData, String>("table", Lists.newArrayList(
                new PropertyColumn<RowData, String>(new ResourceModel("unitLabel"), "label", "label"),
                new PropertyColumn<RowData, String>(new ResourceModel("unitValue"), "value", "value") {
                    @Override
                    public void populateItem(Item<ICellPopulator<RowData>> item, String componentId, IModel<RowData> rowModel) {
                        item.add(new AjaxLazyLoadPanel(componentId, rowModel) {

                            @Override
                            protected boolean isContentReady() {
                                return rowModel.getObject().isReady();
                            }

                            @Override
                            public Component getLazyLoadComponent(String markupId) {
                                IModel model = rowModel.map(row -> row.getValue());
                                IModel<Consumer<AjaxRequestTarget>> consumer = rowModel.map(RowData::getOnclick);
                                Component component = null;
                               /* if (consumer != null && consumer.isPresent().getObject()) {
                                    component = new AjaxLink<Consumer<AjaxRequestTarget>>(markupId,consumer) {
                                        @Override
                                        public void onClick(AjaxRequestTarget target) {
                                            this.getModelObject().accept(target);
                                        }
                                    }.setBody(model);
                                } else {

                                }*/
                                component = new Label(markupId, model);
                                return component;
                            }
                        });
                    }
                }
        ), provider, 50);
        add(table);
        add(new XLSXDataExportLink("excel", table, "МПС"));
        unit.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(table);
            }
        });
        includeUnitChilds.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(table);
            }
        });
        table.setOutputMarkupId(true);
    }


    public List<RowData> createData() {
        List<RowData> data = Lists.newArrayList();
        Specification<Boat> boatAdditionlSpecification = unit.getModel().combineWith(includeUnitChilds.getModel(), (u, i) -> {
            if (i) {
                return u.getAllChildsAndThis();
            } else {
                return Lists.newArrayList(u);
            }
        }).map(uList -> (Specification<Boat>) (r, q, b) -> r.get("unit").in(uList)).getObject();
        RowData row = RowData.create("На учете МПС", () -> boatRepository.registeredCount(boatAdditionlSpecification, getModelObject()));
        /*row.setOnclick((target)->{
            TablePage boatPage = new TablePage(null){
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    visitChildren(FormComponent.class, new IVisitor<FormComponent, Object>() {
                        @Override
                        public void component(FormComponent cmp, IVisit<Object> visit) {
                            if(Objects.equals("unit",cmp.getId())){
                                cmp.setModelObject(unit.getModel().map(Unit::getId).getObject());
                            }
                        }
                    });
                }
            };
            setResponsePage(boatPage);
        });*/
        data.add(row);

        //boats
        data.add(RowData.create("Всего снято с учета", () -> {
            return boatRepository.unregisteredCount(boatAdditionlSpecification, getModelObject());
        }));

        data.add(RowData.create(String.format("Поставлено на учет в %s году", getModel().map(LocalDate::getYear).orElse(null).getObject()), () -> {
            return boatRepository.registeredCount(boatAdditionlSpecification, getModel().map(LocalDate::getYear).getObject());
        }));
        data.add(RowData.create(String.format("Снято с учета в %s году", getModel().map(LocalDate::getYear).orElse(null).getObject()), () -> {
            return boatRepository.unregisteredCount(boatAdditionlSpecification, getModel().map(LocalDate::getYear).getObject());
        }));
//notifications
        Specification<Notification> notificationSpecification = unit.getModel().combineWith(includeUnitChilds.getModel(), (u, i) -> {
            if (i) {
                return u.getAllChildsAndThis();
            } else {
                return Lists.newArrayList(u);
            }
        }).map(uList -> (Specification<Notification>) (r, q, b) -> r.get("unit").in(uList)).getObject();
        data.add(RowData.create("Всего поступило уведомлений", () -> notificationRepository.count(Specification.where(notificationSpecification))));
        data.add(RowData.create(String.format("Поступило уведомлений на %s год", getModel().map(LocalDate::getYear).getObject()), () -> notificationRepository.count(
                Specification.where(notificationSpecification).and((r, q, b) -> b.equal(r.get("year"), getModel().map(LocalDate::getYear).getObject()))
        )));
//exits
        Specification<ExitNotification> s = unit.getModel().combineWith(includeUnitChilds.getModel(), (u, i) -> {
            if (i) {
                return u.getAllChildsAndThis();

            } else {
                return Lists.newArrayList(u);
            }
        }).map(uList -> (Specification<ExitNotification>) (r, q, b) -> r.get("unit").in(uList)).getObject();
        ;
        int hours = 24;
        data.add(RowData.create(String.format("Выходило за последние %s часа", hours), () ->
                exitNotificationRepository.count(Specification.where(s).and((r, q, b) -> {
                    LocalDateTime dateTo = LocalDateTime.now();
                    LocalDateTime dateFrom = dateTo.minusHours(hours);
                    return b.and(b.lessThanOrEqualTo(r.get("exitDateTime"), dateTo),
                            b.greaterThanOrEqualTo(r.get("exitDateTime"), dateFrom));
                }))));
        return data;
    }


    @Data
    @Accessors(chain = true)
    static class RowData implements Serializable {

        String label;

        Object value;

        boolean ready = false;

        private SerializableConsumer<AjaxRequestTarget> onclick = null;

        public RowData(String label) {
            this.label = label;
        }

        static RowData create(String label, Supplier supplier) {
            final RowData result = new RowData(label);
            CompletableFuture.supplyAsync(() -> {
                Object v = supplier.get();
                result.setReady(true);
                return result.setValue(v);
            });
            return result;
        }

        public boolean isReady() {
            return ready;
        }
    }
}
