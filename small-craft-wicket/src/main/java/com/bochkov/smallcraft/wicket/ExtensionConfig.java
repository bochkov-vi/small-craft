package com.bochkov.smallcraft.wicket;

import com.bochkov.wicket.component.Html5AttributesBehavior;
import com.bochkov.wicket.component.select2.Select2ApplicationExtension;
import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension;
import com.giffing.wicket.spring.boot.context.extensions.WicketApplicationInitConfiguration;
import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.protocol.http.WebApplication;

import java.util.Set;

@ApplicationInitExtension
public class ExtensionConfig implements WicketApplicationInitConfiguration {

    @Override
    public void init(WebApplication webApplication) {
        WebjarsSettings settings = new WebjarsSettings();
        WicketWebjars.install(webApplication, settings);
        Select2ApplicationExtension.install(webApplication);
        webApplication.getComponentInstantiationListeners().add(new Html5AttributesBehavior.InstantiationListener());
        webApplication.getComponentInstantiationListeners().add(new IComponentInstantiationListener() {
            @Override
            public void onInstantiation(Component component) {
                if (component instanceof AbstractLink) {
                    component.add(new DisabledAppender());
                }
            }
        });
    }

    static class DisabledAppender extends ClassAttributeModifier {

        Component cmp;

        @Override
        public void bind(Component component) {
            super.bind(component);
            cmp = component;
        }

        @Override
        protected Set<String> update(Set<String> oldClasses) {
            if (!cmp.isEnabled()) {
                oldClasses.add("disabled");
            }
            return oldClasses;
        }
    }
}
