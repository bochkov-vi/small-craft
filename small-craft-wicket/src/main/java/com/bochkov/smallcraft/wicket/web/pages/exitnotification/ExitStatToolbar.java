package com.bochkov.smallcraft.wicket.web.pages.exitnotification;

import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;

public class ExitStatToolbar extends AbstractToolbar {

    @Inject
    ExitNotificationRepository repository;

    IModel<Unit> unit;

    IModel<Boolean> includeChilds;

    public ExitStatToolbar(DataTable<?, ?> table, IModel<Unit> unit, IModel<Boolean> includeChilds) {
        super(table);
        this.unit = unit;
        this.includeChilds = includeChilds;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        ZoneId zoneId = SmallCraftWebSession.get().getZoneId();
        WebMarkupContainer container = new WebMarkupContainer("content");
        add(container);
        container.add(new AttributeAppender("colspan", () -> getTable().getColumns().size()));
        container.add(new LazyPanel("totalOnExit") {

            @Override
            public Serializable loadData() {
                return repository.countTotalOnExit(unit.getObject(), includeChilds.getObject());
            }
        });
        container.add(new LazyPanel("onExitForDay") {

            @Override
            public Serializable loadData() {
                return repository.countOnExitForDay(LocalDate.now(zoneId), unit.getObject(), includeChilds.getObject());
            }
        });
        container.add(new LazyPanel("returnsForDay") {

            @Override
            public Serializable loadData() {
                return repository.countReturnsForDay(LocalDate.now(zoneId), unit.getObject(), includeChilds.getObject());
            }
        });
        container.add(new LazyPanel("totalOnExitLongTime") {

            @Override
            public Serializable loadData() {
                return repository.countTotalOnExitLongTime(LocalDate.now(zoneId), unit.getObject(), includeChilds.getObject());
            }
        });
    }

    abstract class LazyPanel extends Label {


        public LazyPanel(String id) {
            super(id);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            this.setDefaultModel(LoadableDetachableModel.of(this::loadData));
        }

        abstract public Serializable loadData();


    }

}
