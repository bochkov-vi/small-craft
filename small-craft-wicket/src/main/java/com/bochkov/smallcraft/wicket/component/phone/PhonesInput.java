package com.bochkov.smallcraft.wicket.component.phone;

import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.wicket.component.InputMaskBehavior;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import lombok.Getter;
import org.apache.commons.compress.utils.Sets;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PhonesInput extends CompositeInputPanel<Set<String>> {

    @Getter
    FormComponent<String> phoneInput = new TextField<String>("phone", Model.of(), String.class);

    Form form = new Form<Void>("form");

    IModel<List<String>> additionalPhones = new ListModel<>();

    public PhonesInput(String id) {
        super(id);
    }

    public PhonesInput(String id, IModel<Set<String>> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        form.add(phoneInput);
        phoneInput.add(InputMaskBehavior.phone());
        WebMarkupContainer phonesContainer = new WebMarkupContainer("phones-conatiner");
        phonesContainer.setOutputMarkupId(true);
        add(phonesContainer);
        phoneInput.setOutputMarkupId(true);
        add(form);
        form.add(new AjaxSubmitLink("btn-add") {
            @Override
            public void onSubmit(AjaxRequestTarget target) {
                additionalPhones.setObject(additionalPhones.orElseGet(Lists::newArrayList).map(l -> {
                    List<String> list = Lists.newArrayList(l);
                    list.add(phoneInput.getModelObject());
                    return list;
                }).getObject());
                phoneInput.setModelObject(null);
                target.appendJavaScript(jsChangeTrigger());
                target.add(form, phonesContainer);
            }
        });
        phonesContainer.add(new ListView<String>("phones", additionalPhones) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("label", item.getModel()));
                item.add(new AjaxLink<String>("btn-remove", item.getModel()) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        additionalPhones.setObject(additionalPhones.map(l -> {
                            List<String> list = Lists.newArrayList(l);
                            list.remove(getModelObject());
                            return list;
                        }).getObject());
                        target.add(phonesContainer);
                        target.appendJavaScript(jsChangeTrigger());
                    }
                });
            }
        });
    }

    @Override
    public void convertInput() {
        Set<String> set = Streams.concat(Stream.of(phoneInput.getConvertedInput()), additionalPhones.orElseGet(ImmutableList::of).map(Collection::stream).getObject()).filter(Objects::nonNull).collect(Collectors.toSet());
        setConvertedInput(set.isEmpty() ? null : set);
    }

    @Override
    protected void initBeforeRenderer() {
        String phone = getModel().orElseGet(Sets::newHashSet).getObject().stream().filter(Objects::nonNull).findFirst().orElse(null);
        List<String> phones = getModel().orElseGet(Sets::newHashSet).getObject().stream().filter(Objects::nonNull).skip(1).collect(Collectors.toList());
        phoneInput.setModelObject(phone);
        additionalPhones.setObject(phones);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        // response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').on('change',function(){%s})", phoneInput.getMarkupId(), jsChangeTrigger())));
    }

    String jsChangeTrigger() {
        return String.format("$('#%s').trigger('change');", getOutputMarkupId());
    }
}
