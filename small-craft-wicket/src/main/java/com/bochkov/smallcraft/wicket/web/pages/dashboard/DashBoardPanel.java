package com.bochkov.smallcraft.wicket.web.pages.dashboard;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.wicket.component.table.XLSXDataExportLink;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.springframework.data.jpa.domain.Specification;

import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DashBoardPanel extends GenericPanel<LocalDate> {

    @Inject
    BoatRepository boatRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    ExitNotificationRepository exitNotificationRepository;

    public DashBoardPanel(String id) {
        super(id);
    }

    public DashBoardPanel(String id, IModel<LocalDate> model) {
        super(id, model);
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        DataTable table = new DataTable<RowData, String>("table", Lists.newArrayList(
                new PropertyColumn<RowData, String>(new ResourceModel("unitLabel"), "label", "label"),
                new PropertyColumn<RowData, String>(new ResourceModel("unitValue"), "value", "value") {
                    @Override
                    public void populateItem(Item<ICellPopulator<RowData>> item, String componentId, IModel<RowData> rowModel) {
                        item.add(new AjaxLazyLoadPanel<Label>(componentId, rowModel) {

                            @Override
                            protected boolean isContentReady() {
                                return rowModel.getObject().isReady();
                            }

                            @Override
                            public Label getLazyLoadComponent(String markupId) {
                                IModel model = rowModel.map(row -> row.getValue());
                                return new Label(markupId, model);
                            }
                        });
                    }
                }
        ), new ListDataProvider(createData()), 50);
        add(table);
        add(new XLSXDataExportLink("excel",table,"МПС"));
    }

    public List<RowData> createData() {
        List<RowData> data = Lists.newArrayList();
        RowData row = RowData.create("На учете МПС", () -> boatRepository.count((r, q, b) -> b.and(
                b.lessThanOrEqualTo(r.get("registrationDate"), getModelObject()),
                b.or(b.greaterThan(r.get("expirationDate"), getModelObject()), r.get("expirationDate").isNull()))));
        data.add(row);
        Specification<Boat> boatAdditionlSpecification = null;

        //boats
        data.add(RowData.create("Всего снято с учета", () -> {
            return boatRepository.count(Specification.where(boatAdditionlSpecification).and((r, q, b) -> b.and(b.lessThanOrEqualTo(r.get("registrationDate"), getModelObject()),
                    b.or(b.lessThanOrEqualTo(r.get("expirationDate"), getModelObject())))));
        }));
        data.add(RowData.create(String.format("Поставлено на учет в %s году", getModel().map(LocalDate::getYear).orElse(null).getObject()), () -> {
            return boatRepository.count(Specification.where(boatAdditionlSpecification).and((r, q, b) -> b.and(b.lessThanOrEqualTo(r.get("registrationDate"), getModel().map(d -> d.with(TemporalAdjusters.lastDayOfYear())).getObject()),
                    b.greaterThanOrEqualTo(r.get("registrationDate"), getModel().map(d -> d.with(TemporalAdjusters.firstDayOfYear())).getObject()))));
        }));
        data.add(RowData.create(String.format("Снято с учета в %s году", getModel().map(LocalDate::getYear).orElse(null).getObject()), () -> {
            return boatRepository.count(Specification.where(boatAdditionlSpecification).and((r, q, b) -> b.and(b.lessThanOrEqualTo(r.get("expirationDate"), getModel().map(d -> d.with(TemporalAdjusters.lastDayOfYear())).getObject()),
                    b.greaterThanOrEqualTo(r.get("expirationDate"), getModel().map(d -> d.with(TemporalAdjusters.firstDayOfYear())).getObject()))));
        }));
//notifications
        Specification<Notification> notificationSpecification = null;
        data.add(RowData.create("Всего поступило уведомлений", () -> notificationRepository.count(Specification.where(notificationSpecification))));
        data.add(RowData.create(String.format("Поступило уведомлений на %s год", getModel().map(LocalDate::getYear).getObject()), () -> notificationRepository.count(
                Specification.where(notificationSpecification).and((r, q, b) -> b.equal(r.get("year"), getModel().map(LocalDate::getYear).getObject()))
        )));
//exits
        Specification<ExitNotification> s = null;
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
